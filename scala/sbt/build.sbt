val gatlingVersion = "3.16.0"
val gatlingJdbcVersion = "3.16.1"

lazy val gatlingSbtPluginDemo = rootProject
  .enablePlugins(GatlingPlugin)
  .settings(
    name := "gatling-jdbc-demo-sbt-scala",
    scalaVersion := "2.13.18",
    scalacOptions := Seq(
      "-encoding", "UTF-8", "-release:8", "-deprecation",
      "-feature", "-unchecked", "-language:implicitConversions", "-language:postfixOps"),
    libraryDependencies ++= Seq(
      "io.gatling.highcharts" % "gatling-charts-highcharts" % gatlingVersion % "test",
      "io.gatling"            % "gatling-jdbc"            % gatlingJdbcVersion % "test",
      "org.postgresql"        % "postgresql"              % "42.7.14" % "test",
      "io.gatling"            % "gatling-test-framework"  % gatlingVersion % "test")
  )
