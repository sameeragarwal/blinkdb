package shark

import shark.operators._

import org.apache.hadoop.fs.Path
import org.apache.hadoop.hive.conf.HiveConf
import org.apache.hadoop.hive.metastore.api.FieldSchema
import org.apache.hadoop.hive.ql.exec.FetchTask
import org.apache.hadoop.hive.ql.exec.MoveTask
import org.apache.hadoop.hive.ql.exec.TaskFactory
import org.apache.hadoop.hive.ql.optimizer.Optimizer
import org.apache.hadoop.hive.ql.parse._
import org.apache.hadoop.hive.ql.plan.HiveOperation
import org.apache.hadoop.hive.ql.plan.FetchWork
import org.apache.hadoop.hive.ql.plan.PlanUtils
import org.apache.hadoop.hive.ql.plan.MoveWork
import org.apache.hadoop.hive.ql.session.SessionState

//@sameerag
import org.apache.hadoop.hive.ql.costmodel._
import scala.collection.mutable.ListBuffer

import java.util.ArrayList

import scala.collection.JavaConversions._


class SharkSemanticAnalyzer(conf: HiveConf) extends SemanticAnalyzer(conf){

  // TODO(rxin): Why isn't this variable used anywhere?
  var resSchema: java.util.List[FieldSchema] = null

  var moveTasks = new ArrayList[MoveTask]()

  /*
  @SuppressWarnings("nls")
  private void WalkAST(ASTNode ast, List<ASTNode> tableNameList, Bounds bounds) {
	  if (ast != null) {
		  for (int i=0; i<ast.getChildCount(); i++){
			  ASTNode child = (ASTNode) ast.getChild(i);
			  switch (child.getToken().getType()) {
			  case HiveParser.TOK_TABNAME:
				  	ASTNode tableNameNode = (ASTNode) child.getChild(0);
				  	tableNameList.add(tableNameNode);
				  break;
			  case HiveParser.TOK_WITHMAXERROR:
			    ASTNode errorBoundNode = (ASTNode) child.getChild(0);
			    bounds.errorBound = Double.parseDouble(errorBoundNode.token.getText());
			    break;
			  case HiveParser.TOK_INTIME:
			    ASTNode timeBoundNode = (ASTNode) child.getChild(0);	  
			    bounds.timeBound = Double.parseDouble(timeBoundNode.token.getText());
			    break;
			  default:
				  break;
			  }
			  WalkAST(child, tableNameList, bounds);
		  }
	  }
  }
  */

/**
* Walk the AST, and get all the table name nodes in a list. 
*/

def WalkAST(ast: ASTNode, tableNameList: ListBuffer[ASTNode], bounds: Bounds): Unit = {
  if (ast != null) {
    for (i <- 0 to ast.getChildCount() -1){
      var child: ASTNode = ast.getChild(i).asInstanceOf[ASTNode];
      child.getToken().getType() match {
      	case HiveParser.TOK_TABNAME =>
      		var tableNameNode: ASTNode = child.getChild(0).asInstanceOf[ASTNode];
      		tableNameList.add(tableNameNode);
      	case HiveParser.TOK_WITHMAXERROR =>
      		var errorBoundNode: ASTNode = child.getChild(0).asInstanceOf[ASTNode];
      		bounds.errorBound = java.lang.Double.parseDouble(errorBoundNode.token.getText());
      	case HiveParser.TOK_INTIME =>
      		var timeBoundNode: ASTNode = child.getChild(0).asInstanceOf[ASTNode];	  
      		bounds.timeBound = java.lang.Double.parseDouble(timeBoundNode.token.getText());
      	case _ => 
	  }
      
      WalkAST(child, tableNameList, bounds);
    }
  }  
}
  
    /**
   * @name replaceTableNameWithSampleName
   * @author api, sameerag
   * @description Replace the full table name with the sample table name in the AST.
   */
def replaceTableNameWithSampleName(ast: ASTNode) = {

  //var tableNameList: List[ASTNode]  = new ArrayList[ASTNode]();
  var tableNameList = new ListBuffer[ASTNode];
  var timeErrorBound: Bounds  = new Bounds();
	  
  WalkAST(ast, tableNameList, timeErrorBound);
  if (timeErrorBound.isInitialized()) {
    var costModel: CostModel = CostModel.getInstance();
	var iterator: Iterator[ASTNode]  = tableNameList.iterator();
	while (iterator.hasNext()) {
	  var tableNameNode: ASTNode = iterator.next();
	  var sampledTableName: String = costModel.getSampledTableName(timeErrorBound.errorBound, timeErrorBound.timeBound);
	  if (sampledTableName != null) {
	    LOG.info("Replacing table [" + tableNameNode.token.getText() + "] with [" + sampledTableName + "]");
	    tableNameNode.token.setText(sampledTableName);
	  }
	}
  }
}
  
  override def analyzeInternal(ast: ASTNode): Unit = {
    reset()

    val qb = new QB(null, null, false);
    val pctx = getParseContext()
    pctx.setQB(qb)
    pctx.setParseTree(ast)
    init(pctx)
    var child: ASTNode = ast;

    LOG.info("Starting Semantic Analysis");
    
    //@sameerag: Adding Anand's table-rewriting logic
    replaceTableNameWithSampleName(ast);

    // analyze create table command
    if (ast.getToken().getType() == HiveParser.TOK_CREATETABLE) {
      return super.analyzeInternal(ast)
    } else {
      SessionState.get().setCommandType(HiveOperation.QUERY);
    }

    // analyze create view command
    if (ast.getToken().getType() == HiveParser.TOK_CREATEVIEW) {
      return super.analyzeInternal(ast)
    }

    // continue analyzing from the child ASTNode.
    doPhase1(child, qb, initPhase1Ctx());
    LOG.info("Completed phase 1 of Semantic Analysis");

    getMetaData(qb);
    LOG.info("Completed getting MetaData in Semantic Analysis");

    // Save the result schema derived from the sink operator produced
    // by genPlan.  This has the correct column names, which clients
    // such as JDBC would prefer instead of the c0, c1 we'll end
    // up with later.
    val sinkOp = genPlan(qb);
    //resSchema =
    //    convertRowSchemaToViewSchema(opParseCtx.get(sinkOp).getRowResolver());

    var pCtx: ParseContext = getParseContext

    val optm = new Optimizer();
    optm.setPctx(pCtx);
    optm.initialize(conf);
    pCtx = optm.optimize();
    init(pCtx);
    genMapRedTasks(qb)
  }

  def genMapRedTasks(qb: QB): Unit = {
    var fetchTask: FetchTask = null
    var fetchWork: FetchWork = null
    val mvTask = new ArrayList[MoveTask]()
    if (qb.getIsQuery) {
      // Configure FetchTask (used for fetching results to CLIDriver)
      val loadWork = getParseContext.getLoadFileWork.get(0)
      val cols = loadWork.getColumns
      val colTypes = loadWork.getColumnTypes
      val resFileFormat = HiveConf.getVar(conf, HiveConf.ConfVars.HIVEQUERYRESULTFILEFORMAT)
      val resultTab = PlanUtils.getDefaultQueryOutputTableDesc(cols, colTypes, resFileFormat)
      println(new Path(loadWork.getSourceDir))
      fetchWork = new FetchWork(
        new Path(loadWork.getSourceDir).toString, resultTab, qb.getParseInfo.getOuterQueryLimit)
      fetchTask = TaskFactory.get(fetchWork,conf).asInstanceOf[FetchTask]
      setFetchTask(fetchTask)

    } else {
      // Configure MoveTasks
      val fileWork = getParseContext.getLoadFileWork
      val tableWork = getParseContext.getLoadTableWork
      
      tableWork.foreach { ltd => 
        mvTask.add(TaskFactory.get(
          new MoveWork(null, null, ltd, null, false), conf).asInstanceOf[MoveTask])
      }

      fileWork.foreach { lfd =>
        mvTask.add(TaskFactory.get(
          new MoveWork(null, null, null, lfd, false), conf).asInstanceOf[MoveTask])
      }

      moveTasks = mvTask
    }
  }

  override def getResultSchema() = resSchema

}
