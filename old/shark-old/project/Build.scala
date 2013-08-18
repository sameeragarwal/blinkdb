import sbt._
import Keys._
import org.apache.velocity.{Template, VelocityContext}
import org.apache.velocity.app.VelocityEngine
import org.apache.velocity.runtime.RuntimeConstants
import java.io.FileWriter
import java.util.ArrayList


object SharkBuild extends Build {
  
  lazy val root = Project("root", file("."), settings = sharkSettings)

  val genTest = InputKey[Unit]("gen-test","Generates tests based on the velocity template") in Test
  val genTestTask = genTest <<= inputTask { (argTask: TaskKey[Seq[String]]) => 
    
    (argTask, resourceDirectory in Test,
     sourceDirectory in Test) map {
      (args, resourceDir, outputDir) => {
        val resultsDir = resourceDir / "results"
        val logDir = resourceDir / "logs"
        val templateDir = resourceDir / "templates"
        val queryDir = resourceDir / "queries" / "clientpositive"
        val ve = new VelocityEngine()
        ve.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, templateDir.absolutePath)
        ve.init()
        val templates = templateDir * "*.vm"
        templates.get foreach {
          (templateFile) => {
            val template = templateFile.name
            val ClassMatcher = """(.*)[.]vm""".r
            val ClassMatcher(className) = template
            var filteredQueries = queryDir * "*.q"
            if (args.length == 1)
              filteredQueries = filteredQueries ** args(0)
            println(filteredQueries)
            val queries = java.util.Arrays.asList(filteredQueries.get.toArray: _*)
            val ctx = initContext(className, resultsDir, logDir, queries)
            val outFile = outputDir / "scala" / (className + ".scala")
            println(outFile)
            generate(ve,ctx,template,outFile)
          }
        }
      }
    }
  }

  def initContext(className: String, resultsDir: File, logDir: File, queries: java.util.List[File]): VelocityContext = {
    val ctx = new VelocityContext()
    ctx.put("className",className)
    ctx.put("resultsDir",resultsDir)
    ctx.put("logDir",logDir)
    java.util.Collections.sort(queries)
    ctx.put("qfiles", queries)
    ctx
  }
  def generate(ve: VelocityEngine, ctx: VelocityContext, template: String, outFile: File) {
    val t = ve.getTemplate(template);
    val writer = new FileWriter(outFile)
    t.merge(ctx, writer)
    writer.close()
  }
                                     
  //Add genTestTask to sourceGenerators in Test
  val sharkSettings = Defaults.defaultSettings ++ Seq(genTestTask)
  
   
        
}
