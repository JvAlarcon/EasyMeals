BEGIN;

CREATE TABLE payment_status (
  id SERIAL,
  description TEXT NOT NULL UNIQUE,
  PRIMARY KEY(id)
);

INSERT INTO payment_status (description)
VALUES ('CREATED'),
       ('CONFIRMED'),
       ('CANCELED');

CREATE TABLE payments (
  id SERIAL,
  amount INTEGER NOT NULL DEFAULT 0 CONSTRAINT payments_amount_positive CHECK(amount >= 0),
  name VARCHAR(100) NOT NULL,
  card_number VARCHAR(20) NOT NULL,
  expiration VARCHAR(7) NOT NULL,
  card_security_code CHAR(3) NOT NULL,
  status_id INTEGER NOT NULL REFERENCES payment_status(id) ON DELETE CASCADE,
  order_id INTEGER NOT NULL,
  payment_form_id INTEGER NOT NULL,
  PRIMARY KEY (id)
);

-- Because this service is following the microservice architecture, we will not have the relation for order_id or payment_form_id as a tradional monolith would have

COMMIT;
