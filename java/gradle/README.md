# Gatling JDBC - Java with Gradle demo project

This folder contain a Gradle project that shows how to use the Gatling JDBC DSL with Java.

## Usage

First, start the demo database from the root folder of this repository:

```console
docker compose up -d
```

To run the demo simulation, use the `gatlingRun` Gradle task:

```console
./gradlew gatlingRun-io.gatling.jdbc.demo.JdbcDemoSimulation
```
