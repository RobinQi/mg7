Nice.scalaProject

name          := "metagenomica"
organization  := "ohnosequences"
description   := "metagenomica project"

bucketSuffix  := "era7.com"

scalaVersion := "2.11.7"

resolvers := Seq(
  "Era7 public maven releases"  at s3("releases.era7.com").toHttps(s3region.value.toString),
  "Era7 public maven snapshots" at s3("snapshots.era7.com").toHttps(s3region.value.toString)
) ++ resolvers.value

libraryDependencies ++= Seq(
  // APIs:
  "ohnosequences" %% "flash"      % "0.3.0-SNAPSHOT",
  "ohnosequences" %% "blast-api"  % "0.5.0-SNAPSHOT",
  "ohnosequences" %% "fastarious" % "0.2.0",
  // generic tools:
  "ohnosequences" %% "cosas"      % "0.8.0",
  "ohnosequences" %% "datasets"   % "0.3.0",
  "ohnosequences" %% "loquat"     % "2.0.0-simple-mappings-SNAPSHOT",
  "ohnosequences" %% "statika"    % "2.0.0-M5",
  // bundles:
  "ohnosequences-bundles" %% "flash"      % "0.2.0",
  "ohnosequences-bundles" %% "blast"      % "0.3.0",
  "ohnosequences-bundles" %% "bio4j-dist" % "0.1.0",
  // utils:
  // "era7" %% "project-utils" % "0.1.0-M5",
  // testing:
  "org.scalatest" %% "scalatest" % "2.2.5" % Test
)

dependencyOverrides ++= Set(
  "commons-logging"            % "commons-logging"     % "1.1.3",
  "commons-codec"              % "commons-codec"       % "1.7",
  "org.apache.httpcomponents"  % "httpclient"          % "4.5.1",
  "org.slf4j"                  % "slf4j-api"           % "1.7.7"
)



fatArtifactSettings

// copied from bio4j-titan:
mergeStrategy in assembly ~= { old => {
    case "log4j.properties"                       => MergeStrategy.filterDistinctLines
    case PathList("org", "apache", "commons", _*) => MergeStrategy.first
    case x                                        => old(x)
  }
}

enablePlugins(BuildInfoPlugin)
buildInfoPackage := "generated.metadata"
buildInfoObject  := name.value.split("""\W""").map(_.capitalize).mkString
buildInfoOptions := Seq(BuildInfoOption.Traits("ohnosequences.statika.AnyArtifactMetadata"))
buildInfoKeys    := Seq[BuildInfoKey](
  organization,
  version,
  "artifact" -> name.value.toLowerCase,
  "artifactUrl" -> fatArtifactUrl.value
)
