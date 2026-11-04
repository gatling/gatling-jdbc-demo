# Gatling JDBC - Kotlin with Maven demo project

This folder contain a Maven project that shows how to use the Gatling JDBC DSL with Kotlin.

## Usage

First, start the demo database from the root folder of this repository:

```console
docker compose up -d
```

To run the demo simulation, use the `gatling:test` Maven goal:

```console
mvn gatling:test -Dgatling.simulationClass=io.gatling.jdbc.demo.JdbcDemoSimulation
```
