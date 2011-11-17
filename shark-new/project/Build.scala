import sbt._
import Keys._
import org.apache.velocity.{Template, VelocityContext}
import org.apache.velocity.app.VelocityEngine
import org.apache.velocity.runtime.RuntimeConstants
import java.io.FileWriter
import java.util.ArrayList


object SharkBuild extends Build {
  
  lazy val root = Project("root", file("."), settings = sharkSettings)
  
  val genTest = TaskKey[Unit]("gen-test","Generates tests based on the velocity template") in Test
  //  val sourceGen = sourceGenerators in Test <+= (resourceDirectory in Test,
  val genTestTask = genTest <<= (resourceDirectory in Test,
                                 sourceDirectory in Test) map {
    (resourceDir, outputDir) => {
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
          val ctx = initContext(className, resultsDir, logDir, queryDir)
          val outFile = outputDir / "java" / (className + ".java")
          println(outFile)
          generate(ve,ctx,template,outFile)
        }
      }
    }
  }
  def getQueryFiles(queryDir:File): java.util.List[File] = {
    val qFiles = new ArrayList[File]()
    queryDir.listFiles.foreach {
      (qFile) => {
        if (!qFile.isDirectory &&
            qFile.getName.endsWith(".q"))
          qFiles.add(qFile)
      }
      Unit
    }
    qFiles
  }
  def initContext(className: String, resultsDir: File, logDir: File, queryDir: File): VelocityContext = {
    val ctx = new VelocityContext()
    ctx.put("className",className)
    ctx.put("resultsDir",resultsDir)
    ctx.put("logDir",logDir)
    ctx.put("qfiles", getQueryFiles(queryDir))
    ctx
  }
  def generate(ve: VelocityEngine, ctx: VelocityContext, template: String, outFile: File) {
    val t = ve.getTemplate(template);
    val writer = new FileWriter(outFile)
    t.merge(ctx, writer)
    writer.close()
  }
/*  val velocityTemplateDirectoryKey = SettingKey[File]("velocity-template-directories",
                                                      "Location of template files")
  val velocityTemplateDirectories = (velocityTemplateDirectoryKey in Test) <<= resourceDirectory
  val velocitySourceGeneratorTask = 
    sourceGenerators in Test <+= (sourceManaged in Test,
                                  velocityTemplateDirectories in Test) map {
      (outDir,templateDir) => {
        val ve = new VelocityEngine()
        ve.init()
        val t = ve.getTemplate(template)
      }
    }*/
                                     
  //Add genTestTask to sourceGenerators in Test
  val sharkSettings = Defaults.defaultSettings ++ Seq(genTestTask)
  
   
        
}
