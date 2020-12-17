import sbt._

object Dependencies {

  object Versions {
    val sparkVersion          = "2.4.0"
    val scalaTestV            = "3.0.8"
    val hadoopV               = "2.7.0"
    val log4jV                = "2.12.0"
    val orgSlf4jV             = "1.7.25"
    val mariadbJavaClientV    = "2.5.4"
    val h2V                   = "1.4.199"
    val circeV                = "0.12.3"
    val circeConfigV          = "0.7.0"
    val doobieV               = "0.8.6"
    val enumeratumCirceV      = "1.5.21"
    val enumeratumQuillV      = "1.5.14"
    val flywayV               = "5.2.4"
    val kindProjectorV        = "0.10.3"
    val log4catV              = "1.0.1"
    val scalaCheckV           = "1.14.0"
    val catsScalaCheckV       = "0.2.0"
    val http4sV               = "0.20.7"
    val pureConfigV           = "0.13.0"
    val circeShapesV          = "0.7.0"
    val catsV                 = "2.0.0"
    val mouseV                = "0.23"
    val chimneyV              = "0.3.3"
    val osLib                 = "0.6.3"
    val scalaMockV            = "4.4.0"
  }

  object ExclusionRules {
    val javaxServlet          = ExclusionRule(organization = "javax.servlet")
    val orgSlf4j              = ExclusionRule(organization = "org.slf4j")
    val ldapApi               = ExclusionRule(organization = "org.apache.directory.api")
    val orgMortbayJetty       = ExclusionRule(organization = "org.mortbay.jetty")
    val comGoogleCodeFindbugs = ExclusionRule(organization = "com.google.code.findbugs")
    val orgXerial             = ExclusionRule(organization = "org.xerial")
    val awsJdk                = ExclusionRule(organization = "com.amazonaws")
    val spark                 = ExclusionRule(organization = "org.apache.spark")
    val hikari                = ExclusionRule(organization = "com.zaxxer")
    val asm                   = ExclusionRule(organization = "asm")
  }

  import Versions._

  val dbConnectorDependencies = Seq(
    "org.mariadb.jdbc" % "mariadb-java-client" % mariadbJavaClientV // LGPL-2.1
    , "com.h2database"   % "h2"                  % h2V % Test // EPL 1.0, MPL 2.0
    , "org.postgresql" % "postgresql" % "42.2.18"
  )

  val circeDepedencies = Seq(
    "io.circe"     %% "circe-core"           % circeV,
    "io.circe"     %% "circe-generic"        % circeV,
    "io.circe"     %% "circe-literal"        % circeV,
    "io.circe"     %% "circe-parser"         % circeV,
    "io.circe"     %% "circe-generic-extras" % "0.12.2",
    "io.circe"     %% "circe-config"         % circeConfigV,
    "com.beachape" %% "enumeratum-circe"     % enumeratumCirceV,
    "com.beachape" %% "enumeratum-quill"     % enumeratumQuillV
  )

  val doobieDependencies = Seq(
    "org.tpolecat" %% "doobie-core"      % doobieV,
    "org.tpolecat" %% "doobie-hikari"    % doobieV,
    "org.tpolecat" %% "doobie-quill"     % doobieV,
    "org.tpolecat" %% "doobie-scalatest" % doobieV % Test,
    "org.tpolecat" %% "doobie-h2"        % doobieV % Test
    , "org.tpolecat" %% "doobie-postgres"        % doobieV % Test
    , "com.beachape" %% "enumeratum-doobie" % enumeratumQuillV
  )

  val flaywayDependencies = Seq(
    "org.flywaydb" % "flyway-core" % flywayV
  )

  val testDependencies = Seq(
    "org.scalactic"     %% "scalactic"       % scalaTestV, // apache
    "org.scalatest"     %% "scalatest"       % scalaTestV % Test, // apache
    "org.scalacheck"    %% "scalacheck"      % scalaCheckV % Test,
    "io.chrisdavenport" %% "cats-scalacheck" % catsScalaCheckV % Test,
    "org.scalamock"     %% "scalamock"       % scalaMockV % Test
    , "io.cucumber" %% "cucumber-scala" % "6.9.0"
    , "io.cucumber" % "cucumber-junit" % "6.9.0"
    , "junit" % "junit" % "4.12"
    ,  "org.typelevel" %% "scalacheck-effect" % "0.2.0"
    , "com.codecommit" %% "cats-effect-testing-specs2" % "0.2.0" % Test

  )

  val http4sClientDependencies = Seq(
    "org.http4s" %% "http4s-dsl"          % http4sV
    , "org.http4s" %% "http4s-blaze-client" % http4sV
    , "org.http4s"  %% "http4s-circe"     % http4sV // supply some utility methods to convert the Encoder/Decoder of circe to the EntityEncoder/EntityDecoder of http4s
  )

  val catsDependencies = Seq(
    "org.typelevel" %% "cats-core"   % catsV,
    "org.typelevel" %% "cats-free"   % catsV,
    "org.typelevel" %% "cats-effect" % catsV,
    "org.typelevel" %% "mouse"       % mouseV
  )

  val kindDependency =
    ("org.typelevel" %% "kind-projector" % kindProjectorV).cross(CrossVersion.binary)

  val log4catsDependencies = Seq(
    "io.chrisdavenport" %% "log4cats-slf4j" % log4catV // Direct Slf4j Support - Recommended
  )

  val log4jDependencies = Seq(
    "org.apache.logging.log4j" % "log4j-api"        % log4jV,
    "org.apache.logging.log4j" % "log4j-core"       % log4jV,
    "org.apache.logging.log4j" % "log4j-slf4j-impl" % log4jV
  )

  val pureConfigDependencies = Seq(
    "com.github.pureconfig" %% "pureconfig"             % pureConfigV,
    "com.github.pureconfig" %% "pureconfig-cats-effect" % pureConfigV,
    "com.github.pureconfig" %% "pureconfig-enumeratum"  % pureConfigV
  )

  val hadoopClientDependencies = Seq(
    ("org.apache.hadoop" % "hadoop-client" % hadoopV)
//      .excludeAll(
//        ExclusionRules.javaxServlet,
//        ExclusionRules.orgSlf4j,
//        ExclusionRules.ldapApi
//      ) // apache
  )

  val hadoopCommonDependencies = Seq(
    ("org.apache.hadoop" % "hadoop-common" % hadoopV) // apache
//      .excludeAll(
//        ExclusionRules.javaxServlet,
//        ExclusionRules.orgSlf4j,
//        ExclusionRules.ldapApi
//      ) // apache

  )

  val hadoopYarnDependencies = Seq(
    ("org.apache.hadoop" % "hadoop-yarn-api" % hadoopV) // apache
      .excludeAll(
        ExclusionRules.javaxServlet,
        ExclusionRules.orgSlf4j,
        ExclusionRules.ldapApi
      ) // apache

  )

  val chimneyDependencies = Seq(
    "io.scalaland" %% "chimney" % chimneyV
  )

}
