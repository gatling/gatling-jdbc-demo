package io.gatling.jdbc.demo

import io.gatling.javaapi.core.*
import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.jdbc.JdbcDsl.*

class JdbcDemoSimulation : Simulation() {

  companion object {
    // Start the database first with: docker compose up -d
    // (from the root folder of this repository)
    const val URL = "jdbc:postgresql://localhost:5432/shop"
    const val USERNAME = "gatling"
    const val PASSWORD = "gatling"
  }

  val jdbcProtocol = jdbc
    .url(URL)
    .credentials(USERNAME, PASSWORD)
    // the size of the connection pool, like in an application
    .maxConnections(5)

  // loaded from the database when the simulation starts
  val customers = jdbcFeeder(URL, USERNAME, PASSWORD, "SELECT id AS \"customerId\" FROM customers").random()

  val categories = listFeeder(
    listOf("books", "games", "music", "movies", "toys").map { mapOf<String, Any>("category" to it) }
  ).random()

  val browse = feed(categories)
    .exec(
      jdbc("Browse category")
        .preparedStatement("SELECT id, name, price FROM products WHERE category = ? ORDER BY name LIMIT 10")
        .params("#{category}")
        .check(
          rowCount().gt(0),
          column("id").findRandom().saveAs("productId")
        )
    )
    .pause(1)
    .exec(
      jdbc("Product details")
        .preparedStatement("SELECT price, stock FROM products WHERE id = ?")
        .params("#{productId}")
        .check(column("price").saveAs("price"))
    )

  // all or nothing: the order, its line, and the stock
  val order = exec(
    jdbc("Begin").begin(),
    jdbc("Create order")
      .preparedStatement("INSERT INTO orders (customer_id, total) VALUES (?, ?)")
      .params("#{customerId}", "#{price}")
      .returnGeneratedKeys("id")
      .check(generatedKey("id").saveAs("orderId")),
    jdbc("Add order line")
      .preparedStatement("INSERT INTO order_lines (order_id, product_id, quantity, price) VALUES (?, ?, 1, ?)")
      .params("#{orderId}", "#{productId}", "#{price}")
      .check(updateCount().shouldBe(1)),
    jdbc("Update stock")
      .preparedStatement("UPDATE products SET stock = stock - 1 WHERE id = ? AND stock > 0")
      .params("#{productId}")
      .check(updateCount().shouldBe(1)),
    jdbc("Commit").commit()
  )

  val history = exec(
    jdbc("Order history")
      .preparedStatement("SELECT id, placed_at, total FROM orders WHERE customer_id = ? ORDER BY placed_at DESC LIMIT 5")
      .params("#{customerId}")
      .check(rowCount().gt(0))
  )

  val scn = scenario("Shop")
    .feed(customers)
    .repeat(5).on(
      exec(browse)
        .pause(1)
        .exec(order)
        .pause(1)
        .exec(history)
    )

  // ./gradlew gatlingRun --simulation io.gatling.jdbc.demo.JdbcDemoSimulation

  init {
    setUp(
      scn.injectOpen(atOnceUsers(5))
    ).protocols(jdbcProtocol)
  }
}
