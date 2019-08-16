name := "assignment01"

version := "0.1"

scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-java8-compat" % "0.9.0",
  "org.springframework.boot" % "spring-boot-starter-web" % "2.1.7.RELEASE",
  "org.springframework.boot" % "spring-boot-starter-data-jpa" % "2.1.7.RELEASE",
  "org.springframework.boot" % "spring-boot-devtools" % "2.1.7.RELEASE",
  "org.springframework.boot" % "spring-boot-starter-test" % "2.1.7.RELEASE",
  "org.scalaj" %% "scalaj-http" % "2.4.2",
  "org.scalactic" %% "scalactic" % "3.1.0-RC1",
  "com.h2database" % "h2" % "1.4.199",
  "com.github.tototoshi" %% "scala-csv" % "1.3.6",
  "org.bitbucket.eunjeon" %% "seunjeon" % "1.5.0" exclude("com.jsuereth", "sbt-pgp"),
  "com.typesafe.play" %% "play-json" % "2.7.3",
  "org.hibernate" % "hibernate-validator" % "6.0.17.Final",
  "io.jsonwebtoken" % "jjwt" % "0.9.1",
  "org.scalatest" %% "scalatest" % "3.1.0-RC1" % Test,
  "com.novocode" % "junit-interface" % "0.11" % Test exclude("junit", "junit-dep"),
  "org.springframework.boot" % "spring-boot-configuration-processor" % "2.1.7.RELEASE"
)

mainClass in (Compile, run) := Some("ijmo.kakaopay.financialassistance.FinancialAssistanceApp")