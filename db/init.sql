-- The shop the demo simulations run against: customers, products, and their orders.

CREATE TABLE customers (
  id    SERIAL PRIMARY KEY,
  email TEXT NOT NULL UNIQUE,
  name  TEXT NOT NULL
);

CREATE TABLE products (
  id       SERIAL PRIMARY KEY,
  name     TEXT NOT NULL,
  category TEXT NOT NULL,
  price    NUMERIC(10, 2) NOT NULL,
  stock    INT NOT NULL
);

CREATE TABLE orders (
  id          BIGSERIAL PRIMARY KEY,
  customer_id INT NOT NULL REFERENCES customers (id),
  placed_at   TIMESTAMP NOT NULL DEFAULT now(),
  total       NUMERIC(10, 2) NOT NULL DEFAULT 0
);

CREATE TABLE order_lines (
  order_id   BIGINT NOT NULL REFERENCES orders (id),
  product_id INT NOT NULL REFERENCES products (id),
  quantity   INT NOT NULL,
  price      NUMERIC(10, 2) NOT NULL
);

CREATE INDEX orders_customer_id ON orders (customer_id);
CREATE INDEX products_category ON products (category);

INSERT INTO customers (email, name)
SELECT 'customer' || i || '@example.com', 'Customer ' || i
FROM generate_series(1, 100) AS i;

INSERT INTO products (name, category, price, stock)
SELECT 'Product ' || i, (ARRAY['books', 'games', 'music', 'movies', 'toys'])[1 + i % 5], round((5 + i * 1.25)::NUMERIC, 2), 1000000
FROM generate_series(1, 50) AS i;
