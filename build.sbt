organization in ThisBuild := "com.example"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.11.8"

lazy val `lagom-java-workshop` = (project in file("."))
  .aggregate(
    `basket-api`, `basket-impl`,
    `inventory-api`, `inventory-impl`
    //    , `order-api`, `order-impl`
    //    , `delivery-api`, `delivery-impl`
  )

lazy val `basket-api` = (project in file("basket-api"))
  .settings(common: _*)
  .settings(
    libraryDependencies ++= Seq(
      lagomJavadslApi,
      lombok
    )
  )

lazy val `basket-impl` = (project in file("basket-impl"))
  .enablePlugins(LagomJava)
  .settings(common: _*)
  .settings(
    libraryDependencies ++= Seq(
      lagomJavadslPersistenceCassandra,
      lagomJavadslKafkaBroker,
      lagomJavadslTestKit,
      lagomJavadslJackson,
      lombok
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`basket-api`, `inventory-api`)

lazy val `inventory-api` = (project in file("inventory-api"))
  .settings(common: _*)
  .settings(
    libraryDependencies ++= Seq(
      lagomJavadslApi,
      lombok
    )
  )


lazy val `inventory-impl` = (project in file("inventory-impl"))
  .enablePlugins(LagomJava)
  .settings(common: _*)
  .settings(
    libraryDependencies ++= Seq(
      lagomJavadslPersistenceCassandra,
      lagomJavadslKafkaBroker,
      lagomJavadslTestKit,
      lagomJavadslJackson,
      lombok
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`inventory-api`, `basket-api`)



val lombok = "org.projectlombok" % "lombok" % "1.16.10"

def common = Seq(
  javacOptions in compile += "-parameters"
)

