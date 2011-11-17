name := "Shark"

version := "1.0"

organization := "org.shark"

scalaVersion := "2.9.1"

unmanagedJars in Compile <++= baseDirectory map { base =>
    val hiveFile = file(System.getenv("HIVE_HOME")) / "lib"
    val baseDirectories = (base / "lib") +++ (hiveFile)
    val customJars = (baseDirectories ** "*.jar")
    customJars.classpath
}
