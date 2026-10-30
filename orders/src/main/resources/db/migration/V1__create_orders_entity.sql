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
  quantity INTEGER NOT NULL CONSTRAINT quantity_positive CHECK (quantity > 0),
  order_id INTEGER NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
  PRIMARY KEY(id)
);

-- Trigger to prevent order having no items on insert
CREATE FUNCTION check_order_has_items() RETURNS trigger AS $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM order_items WHERE order_id = NEW.id) THEN
    RAISE EXCEPTION 'Order % must have at least one item', NEW.id
      USING ERRCODE = 'check_violation',
        CONSTRAINT = 'order_must_have_items',
	TABLE = TG_TABLE_NAME,
	DETAIL = format('order_id=%s', NEW.id),
	HINT = 'Insert the order and its items in the same transaction.';
  END IF;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE CONSTRAINT TRIGGER trg_order_has_items
AFTER INSERT ON orders
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW EXECUTE FUNCTION check_order_has_items();

-- Trigger to prevent deleting all items from an order without deleting the order
-- The trigger apply for updated too, to prevent letting an order without items
CREATE FUNCTION check_order_keeps_items() RETURNS trigger AS $$
BEGIN
  -- Lock the order to prevent concurrent transaction issues
  PERFORM 1 FROM orders WHERE id = OLD.order_id FOR UPDATE;
  
  IF FOUND
    AND NOT EXISTS (SELECT 1 FROM order_items WHERE order_id = OLD.order_id)
  THEN
    RAISE EXCEPTION 'Order % must have a least one item', OLD.order_id
      USING ERRCODE = 'check_violation',
        CONSTRAINT = 'order_must_have_items',
	TABLE = TG_TABLE_NAME,
	DETAIL = format('order_id=%s', OLD.order_id),
	HINT = 'Cant let an order without any items, the order should have at least one item';
  END IF;
  RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE CONSTRAINT TRIGGER trg_order_keeps_items
AFTER DELETE OR UPDATE OF order_id ON order_items
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW EXECUTE FUNCTION check_order_keeps_items();

COMMIT;
