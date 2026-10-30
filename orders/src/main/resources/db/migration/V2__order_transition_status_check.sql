BEGIN;

-- Entity that control transitioning of status
CREATE TABLE order_status_transition (
  from_status_id INTEGER NOT NULL REFERENCES order_status(id),
  to_status_id INTEGER NOT NULL REFERENCES order_status(id),
  PRIMARY KEY(from_status_id, to_status_id)
);

-- Transition order
-- Placed -> Cancelled, Paid, Not Authorized
-- Paid -> Confirmed, Cancelled
-- Confirmed -> Ready, Cancelled
-- Ready -> Shipped
-- Shipped -> Delivered
-- Any other transition is not allowed
INSERT INTO order_status_transition (from_status_id, to_status_id) VALUES
  (1, 2), (1, 3), (1, 4),
  (3, 5), (3, 2),
  (5, 6), (5, 2),
  (6, 7),
  (7, 8);

CREATE FUNCTION enforce_order_status_transition() RETURNS trigger AS $$
DECLARE
  from_description TEXT;
  to_description TEXT;
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM order_status_transition
    WHERE from_status_id = OLD.status_id
      AND to_status_id = NEW.status_id)
  THEN
    SELECT description FROM order_status
    WHERE id = OLD.status_id
    INTO from_description;

    SELECT description FROM order_status
    WHERE id = NEW.status_id
    INTO to_description;

    RAISE EXCEPTION 'Invalid order status transition: from % (%) to % (%)',
    OLD.status_id, from_description,
    NEW.status_id, to_description
      USING ERRCODE = 'check_violation',
      CONSTRAINT = 'order_status_trasition_allowed',
      TABLE = TG_TABLE_NAME,
      DETAIL = format('order_id=%s', OLD.id);
  END IF;

  NEW.updated_at := CURRENT_TIMESTAMP;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_order_status_transition
BEFORE UPDATE ON orders
FOR EACH ROW EXECUTE FUNCTION enforce_order_status_transition();

COMMIT;
