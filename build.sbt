name := """vote"""

version := "2.5.1"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  "commons-collections" % "commons-collections" % "3.2.1",
  "org.mariadb.jdbc" % "mariadb-java-client" % "1.1.8",
  "org.mindrot" % "jbcrypt" % "0.3m"
)
