package io.gatling.jdbc.demo

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.jdbc.Predef._

class JdbcDemoSimulation extends Simulation {

  // Start the database first with: docker compose up -d
  // (from the root folder of this repository)
  private val Url = "jdbc:postgresql://localhost:5432/shop"
  private val Username = "gatling"
  private val Password = "gatling"

  private val jdbcProtocol = jdbc
    .url(Url)
    .credentials(Username, Password)
    // the size of the connection pool, like in an application
    .maxConnections(5)

  // loaded from the database when the simulation starts
  private val customers = jdbcFeeder(Url, Username, Password, """SELECT id AS "customerId" FROM customers""").random

  private val categories = Array("books", "games", "music", "movies", "toys").map(category => Map("category" -> category)).random

  private val browse = feed(categories)
    .exec(
      jdbc("Browse category")
        .preparedStatement("SELECT id, name, price FROM products WHERE category = ? ORDER BY name LIMIT 10")
        .params("#{category}")
        .check(
          rowCount.gt(0),
          column("id").findRandom.saveAs("productId")
        )
    )
    .pause(1.second)
    .exec(
      jdbc("Product details")
        .preparedStatement("SELECT price, stock FROM products WHERE id = ?")
        .params("#{productId}")
        .check(column("price").saveAs("price"))
    )

  // all or nothing: the order, its line, and the stock
  private val order = exec(
    jdbc("Begin").begin(),
    jdbc("Create order")
      .preparedStatement("INSERT INTO orders (customer_id, total) VALUES (?, ?)")
      .params("#{customerId}", "#{price}")
      .returnGeneratedKeys("id")
      .check(generatedKey("id").saveAs("orderId")),
    jdbc("Add order line")
      .preparedStatement("INSERT INTO order_lines (order_id, product_id, quantity, price) VALUES (?, ?, 1, ?)")
      .params("#{orderId}", "#{productId}", "#{price}")
      .check(updateCount.is(1)),
    jdbc("Update stock")
      .preparedStatement("UPDATE products SET stock = stock - 1 WHERE id = ? AND stock > 0")
      .params("#{productId}")
      .check(updateCount.is(1)),
    jdbc("Commit").commit()
  )

  private val history = exec(
    jdbc("Order history")
      .preparedStatement("SELECT id, placed_at, total FROM orders WHERE customer_id = ? ORDER BY placed_at DESC LIMIT 5")
      .params("#{customerId}")
      .check(rowCount.gt(0))
  )

  private val scn = scenario("Shop")
    .feed(customers)
    .repeat(5)(
      exec(browse)
        .pause(1.second)
        .exec(order)
        .pause(1.second)
        .exec(history)
    )

  // sbt "Gatling / testOnly io.gatling.jdbc.demo.JdbcDemoSimulation"

  setUp(
    scn.inject(atOnceUsers(5))
  ).protocols(jdbcProtocol)
}
