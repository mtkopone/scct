organization := "reaktor"

name := "scct"

version := "0.2-SNAPSHOT"

scalaVersion := "2.10.1"

libraryDependencies <+= (scalaVersion) { v =>
  "org.scala-lang" % "scala-compiler" % v % "provided"
}

libraryDependencies ++= Seq(
  "junit" % "junit" % "4.10" % "test",
  "org.mockito" % "mockito-all" % "1.9.5" % "test" withSources,
  "org.specs2" %% "specs2" % "1.14" % "test"
)

publishTo := Some(Resolver.file("file",  new File("../gh-pages/maven-repo")))

resolvers += "scala-tools-releases" at "https://oss.sonatype.org/content/groups/scala-tools/"

testOptions in Test <+= (scalaVersion in Test) map { (scalaVer) => Tests.Setup { () => System.setProperty("scct-test-scala-version", scalaVer) } }
