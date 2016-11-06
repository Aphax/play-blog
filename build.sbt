name := "BlogMVC"

version := "1.0"

lazy val `blogmvc` = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.8"

unmanagedResourceDirectories in Test <+= baseDirectory(_ / "target/web/public/test")

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

// Project dependencies (ivy or maven artefacts)
libraryDependencies ++= Seq(
  // Internal dependencies directly handled by Play
  jdbc, javaJpa, cache, ws, specs2 % Test,
  // The core O/RM functionality as provided by Hibernate
  "org.hibernate" % "hibernate-core" % "5.2.+",
  // Temporary fix to an sbt bug since hibernate 5.2
  // Stack overflow topic : http://stackoverflow.com/questions/38278199/play-framework-inject-error
  // Bug report : https://github.com/sbt/sbt/issues/1431
  "dom4j" % "dom4j" % "1.6.+" intransitive(),
  // MySQL JDBC Type 4 driver
  "mysql" % "mysql-connector-java" % "5.1.+",
  // Jadira Usertype Core (for Joda Time, Joda Money, Libphonenum And JDK Types With Hibernate)
  "org.jadira.usertype" % "usertype.core" % "6.0.+",
  // A Java 1.6+ library providing a clean and lightweight markdown processor
  "org.pegdown" % "pegdown" % "1.6.+"
)

// Hack : Running Play in development mode while using JPA
// See : https://playframework.com/documentation/2.5.x/JavaJPA#deploying-play-with-jpa
PlayKeys.externalizeResources := false