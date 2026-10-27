BEGIN;

CREATE TABLE order_status (
  id SERIAL,
  description TEXT NOT NULL UNIQUE,
  PRIMARY KEY(id)
);

INSERT INTO order_status (description)
VALUES ('PLACED'),
       ('CANCELLED'),
       ('PAID'),
       ('NOT_AUTHORIZED'),
       ('CONFIRMED'),
       ('READY'),
       ('SHIPPED'),
       ('DELIVERED');

CREATE TABLE orders (
  id SERIAL,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  status_id INTEGER NOT NULL REFERENCES order_status(id) ON DELETE RESTRICT,
  PRIMARY KEY (id)
);

CREATE TABLE order_items (
  id SERIAL,
  description TEXT NOT NULL,
  quantity INTEGER NOT NULL ADD CONSTRAINT quantity_positive CHECK (quantity > 0),
  order_id INTEGER NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
  PRIMARY KEY(id)
);

COMMIT;
