resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"
resolvers += "Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject"      % "0.4.0")
addSbtPlugin("org.portable-scala" % "sbt-scala-native-crossproject" % "0.4.0")
addSbtPlugin("org.scala-js"       % "sbt-scalajs"                   % "0.6.23")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "2.3")
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.1.1")
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")
addSbtPlugin("pl.project13.scala" % "sbt-jmh" % "0.3.3")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.6")
addSbtPlugin("io.spray" % "sbt-revolver" % "0.9.1")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")
addSbtPlugin("com.codacy" % "sbt-codacy-coverage" % "1.3.12")