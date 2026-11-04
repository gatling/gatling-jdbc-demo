# Gatling JDBC - Scala with SBT demo project

This folder contain a SBT project that shows how to use the Gatling JDBC DSL with Scala.

## Usage

First, start the demo database from the root folder of this repository:

```console
docker compose up -d
```

To run the demo simulation, use the `Gatling / testOnly` SBT task:

```console
sbt "Gatling / testOnly io.gatling.jdbc.demo.JdbcDemoSimulation"
```
