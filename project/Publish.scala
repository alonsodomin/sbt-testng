import sbt._
import sbt.Keys._
import bintray.BintrayPlugin.bintrayPublishSettings
import bintray.BintrayKeys.{bintray, bintrayRepository, bintrayOrganization}

object Publish {
  lazy val noArtifactsAndPublishingSettings: Seq[Setting[_]] = Seq(
    // No, SBT, we don't want any artifacts for root.
    // No, not even an empty jar.
    // Invoking Cthulhu:
    packageBin in Global := file(""),
    packagedArtifacts    := Map(),
    publish              := {},
    publishLocal         := {},
    publishArtifact      := false,
    Keys.`package`       := file("")
  )

  lazy val libraryPublishSettings: Seq[Setting[_]] = Seq(
    publishArtifact in Test := false,
    publishMavenStyle := true,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
      },
    pomExtra := pomData,
    pomIncludeRepository := { _ => false }
  )

  lazy val pluginPublishSettings: Seq[Setting[_]] = bintrayPublishSettings ++ libraryPublishSettings ++ Seq(
    publishArtifact in Test := false,
    publishMavenStyle       := true,

    bintrayRepository       := "sbt-plugins",
    bintrayOrganization     := None
  )

  // Had to look up Maven documentation on the difference between developer and contributor.
  //
  // I would add everyone on this list as a developer but in the interest of reducing the amount of
  // questions/reuqests/... accidentally heading their way, I move them to contributors.
  //
  // Maven doc about the developers section:
  //   "A good rule of thumb is, if the person should not be contacted about the project, they need not be listed here."
  lazy val pomData =
    <scm>
      <url>git@github.com:soc/scala-java-time.git</url>
      <connection>scm:git:git@github.com:soc/scala-java-time.git</connection>
    </scm>
    <developers>
      <developer>
        <id>soc</id>
        <name>Simon Ochsenreither</name>
        <url>https://github.com/soc</url>
      </developer>
    </developers>
    <contributors>
      <contributor> <!-- Scala.js Junit support -->
        <name>Nicolas Stucki</name>
        <url>https://github.com/nicolasstucki</url>
      </contributor>
      <contributor> <!-- Scala.js SBT build -->
        <name>Sébastien Doeraene</name>
        <url>https://github.com/sjrd</url>
      </contributor>
      <contributor> <!-- Original sbt-testng developer -->
        <name>Joachim Hofer</name>
        <url>https://github.com/jmhofer</url>
      </contributor>
      <contributor> <!-- Original sbt-testng developer -->
        <name>Andreas Flierl</name>
        <url>https://github.com/asflierl</url>
      </contributor>
    </contributors>
}
