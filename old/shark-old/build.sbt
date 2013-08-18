name := "Shark"

version := "1.0"

organization := "org.shark"

scalaVersion := "2.9.1"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "1.6.1" % "test",
  "junit" % "junit" % "4.10" % "test"
)

unmanagedJars in Compile <++= baseDirectory map { base =>
    val hiveFile = file(System.getenv("HIVE_HOME")) / "lib"
    val baseDirectories = (base / "lib") +++ (hiveFile)
    val customJars = (baseDirectories ** "*.jar")
    customJars.classpath
}

unmanagedClasspath in Test <+= (resourceDirectory in Test) map { dir => Attributed.blank(dir / "conf") }

testOptions in Test += Tests.Setup( () => {
  System.setProperty("test.output.overwrite","false")
  System.setProperty("test.tmp.dir","/tmp")
  System.setProperty("build.dir","/home/cengle/hive-0.7.0/src/build/ql")
  System.setProperty("build.dir.hive","/home/cengle/hive-0.7.0/src/build")
})


