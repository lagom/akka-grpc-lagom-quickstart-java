import akka.grpc.gen.javadsl.play.{ PlayJavaClientCodeGenerator, PlayJavaServerCodeGenerator }
import sbt.Def
import sbt.Keys.dependencyOverrides

organization in ThisBuild := "com.example"
version in ThisBuild := "1.0-SNAPSHOT"

// the Java version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.12.4"

val lombok = "org.projectlombok" % "lombok" % "1.16.18"

lazy val `akka-grpc-lagom-quickstart-java` = (project in file("."))
  .aggregate(`hello-api`, `hello-impl`,
//    `hello-proxy-api`, `hello-proxy-impl`
  )

lazy val `hello-api` = (project in file("hello-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomJavadslApi
    )
  )

// ALL SETTINGS HERE ARE TEMPORARY WORKAROUNDS FOR KNOWN ISSUES OR WIP
def workaroundSettings: Seq[sbt.Setting[_]] = Seq(
  // This quickstart still doesn't use service discovery for the gRPC client
  // so we hardcode the HTTPS port for the gRPC server to 11000
  lagomServiceHttpsPort := 11000,
  // There is a bug in akka-http 10.1.4 that makes it not work with gRPC+Lagom,
  // so we need to downgrade to 10.1.3 (or move to 10.1.5 when that's out)
  // https://github.com/akka/akka-http/issues/2168
  dependencyOverrides += "com.typesafe.akka" %% "akka-http-core" % "10.1.3",
  dependencyOverrides += "com.typesafe.akka" %% "akka-http" % "10.1.3"
)

lazy val `hello-impl` = (project in file("hello-impl"))
  .enablePlugins(LagomJava)
  .enablePlugins(AkkaGrpcPlugin) // enables source generation for gRPC
  .enablePlugins(PlayAkkaHttp2Support) // enables serving HTTP/2 and gRPC
  .settings(
  akkaGrpcGeneratedLanguages := Seq(AkkaGrpc.Java),
  akkaGrpcGeneratedSources :=
    Seq(
      AkkaGrpc.Server,
      AkkaGrpc.Client // the client is only used in tests. See https://github.com/akka/akka-grpc/issues/410
    ),
  akkaGrpcExtraGenerators in Compile += PlayJavaServerCodeGenerator,
).settings(
  workaroundSettings: _*
).settings(
  libraryDependencies ++= Seq(
    lagomJavadslTestKit,
    lagomLogback,
    lombok
  )
).settings(lagomForkedTestSettings: _*)
  .dependsOn(`hello-api`)

//lazy val `hello-proxy-api` = (project in file("hello-proxy-api"))
//  .settings(
//    libraryDependencies ++= Seq(
//      lagomJavadslApi
//    )
//  )
//
//lazy val `hello-proxy-impl` = (project in file("hello-proxy-impl"))
//  .enablePlugins(LagomJava)
//  .enablePlugins(AkkaGrpcPlugin) // enables source generation for gRPC
//  .settings(
//  akkaGrpcGeneratedLanguages := Seq(AkkaGrpc.Java),
//  //  akkaGrpcExtraGenerators += PlayJavaClientCodeGenerator,
//).settings(
//  libraryDependencies ++= Seq(
//    lagomJavadslTestKit,
//    lagomLogback,
//    lombok
//  )
//)
//  .dependsOn(`hello-proxy-api`, `hello-api`)

lagomCassandraEnabled in ThisBuild := false
lagomKafkaEnabled in ThisBuild := false
