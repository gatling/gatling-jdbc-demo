# Gatling JDBC demo

This demo project contains all combination of currently supported languages and build tools:

- [Java with Gradle](https://github.com/gatling/gatling-jdbc-demo/tree/main/java/gradle)
- [Java with Maven](https://github.com/gatling/gatling-jdbc-demo/tree/main/java/maven)
- [JavaScript](https://github.com/gatling/gatling-jdbc-demo/tree/main/javascript)
- [Kotlin with Gradle](https://github.com/gatling/gatling-jdbc-demo/tree/main/kotlin/gradle)
- [Kotlin with Maven](https://github.com/gatling/gatling-jdbc-demo/tree/main/kotlin/maven)
- [Scala with SBT](https://github.com/gatling/gatling-jdbc-demo/tree/main/scala/sbt)
- [TypeScript](https://github.com/gatling/gatling-jdbc-demo/tree/main/typescript)

The simulations run against a small shop database, with customers, products, and orders, in a local PostgreSQL started
with Docker:

```console
docker compose up -d
```

The database is created and filled by [db/init.sql](db/init.sql). Stop it with `docker compose down`.

Each virtual user picks a random customer from the database with the JDBC feeder, then browses products, places orders
in transactions, and checks its order history.

Check each project for their usage details, and the [Gatling JDBC documentation](https://docs.gatling.io/reference/script/jdbc/).
