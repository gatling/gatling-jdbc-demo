# Gatling JDBC - TypeScript demo project

This folder contain a TypeScript project that shows how to use the Gatling JDBC DSL.

The PostgreSQL JDBC driver is declared in the `gatling.jvmDependencies` section of `package.json`.

## Usage

First, start the demo database from the root folder of this repository:

```console
docker compose up -d
```

To run the demo simulation, use the `run` command:

```console
npm install
npx gatling run --simulation jdbc
```
