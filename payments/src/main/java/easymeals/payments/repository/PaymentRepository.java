package easymeals.payments.repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import easymeals.payments.model.Payment;
import easymeals.payments.model.Status;

// This class uses JDBC strategy instead of JPA
@Repository
public class PaymentRepository {
    
    private final JdbcClient jdbc;
    
    public PaymentRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }
    
    public long count() {
        String query = "SELECT COUNT(id) FROM payments";
        return jdbc.sql(query).query(Long.class).single();
    }
    
    public List<Payment> findAll(Pageable pageable) {
        String query = 
            """
            SELECT id, amount, name, card_number, expiration,
                card_security_code, status_id, order_id, payment_form_id
            FROM payments
            ORDER BY id DESC
            LIMIT :limit
            OFFSET :offset
            """;
        return jdbc.sql(query)
            .param("limit", pageable.getPageSize())
            .param("offset", pageable.getOffset())
            .query((rs, _) -> new Payment(
                rs.getLong("id"),
                BigInteger.valueOf(rs.getInt("amount")),
                rs.getString("name"),
                rs.getString("card_number"),
                rs.getString("expiration"),
                rs.getString("card_security_code"),
                Status.fromId(rs.getInt("status_id")),
                rs.getLong("order_id"),
                rs.getLong("payment_form_id")))
            .list();
    }
    
    public Optional<Payment> findById(Long id) {
        String query =
            """
            SELECT id, amount, name, card_number, expiration,
                card_security_code, status_id, order_id, payment_form_id
            FROM payments
            WHERE id = :id
            """
            + "";
        return jdbc.sql(query)
            .param("id", id)
            .query((rs, _) -> new Payment(
                rs.getLong("id"),
                BigInteger.valueOf(rs.getInt("amount")),
                rs.getString("name"),
                rs.getString("card_number"),
                rs.getString("expiration"),
                rs.getString("card_security_code"),
                Status.fromId(rs.getInt("status_id")),
                rs.getLong("order_id"),
                rs.getLong("payment_form_id")
            ))
            .optional();
    }
    
    public @NonNull Payment insert(Payment payment) {
        String query =
            """
            INSERT INTO payments
                (amount, name, card_number, expiration, card_security_code, status_id, order_id, payment_form_id)
            VALUES
                (:amount, :name, :cardNumber, :expiration, :cardSecurityCode, :statusId, :orderId, :paymentFormId)
            RETURNING id, amount, name, card_number, expiration,
                card_security_code, status_id, order_id, payment_form_id
            """;
        return jdbc.sql(query)
            .param("amount", payment.amount())
            .param("name", payment.name())
            .param("cardNumber", payment.cardNumber())
            .param("expiration", payment.expiration())
            .param("cardSecurityCode", payment.cardSecurityCode())
            .param("statusId", payment.status().getId())
            .param("orderId", payment.orderId())
            .param("paymentFormId", payment.paymentFormId())
            .query((rs, _) -> new Payment(
                rs.getLong("id"),
                BigInteger.valueOf(rs.getInt("amount")),
                rs.getString("name"),
                rs.getString("card_number"),
                rs.getString("expiration"),
                rs.getString("card_security_code"),
                Status.fromId(rs.getInt("status_id")),
                rs.getLong("order_id"),
                rs.getLong("payment_form_id")))
            .single();
    }
    
    public @NonNull Payment update(Payment payment) {
        String query =
            """
            UPDATE payments
            SET amount = COALESCE(:amount, amount),
                name = COALESCE(:name, name),
                card_number = COALESCE(:cardNumber, card_number),
                expiration = COALESCE(:expiration, expiration),
                card_security_code = COALESCE(:cardSecurityCode, card_security_code),
                status_id = COALESCE(:statusId, status_id),
                order_id = COALESCE(:orderId, order_id),
                payment_form_id = COALESCE(:paymentFormId, payment_form_id)
            WHERE id = :id
            RETURNING id, amount, name, card_number, expiration,
                card_security_code, status_id, order_id, payment_form_id
            """;
        return jdbc.sql(query)
            .param("id", payment.id())
            .param("amount", payment.amount())
            .param("name", payment.name())
            .param("cardNumber", payment.cardNumber())
            .param("expiration", payment.expiration())
            .param("cardSecurityCode", payment.cardSecurityCode())
            .param("statusId", payment.status().getId())
            .param("orderId", payment.orderId())
            .param("paymentFormId", payment.paymentFormId())
            .query((rs, _) -> new Payment(
                rs.getLong("id"),
                BigInteger.valueOf(rs.getInt("amount")),
                rs.getString("name"),
                rs.getString("card_number"),
                rs.getString("expiration"),
                rs.getString("card_security_code"),
                Status.fromId(rs.getInt("status_id")),
                rs.getLong("order_id"),
                rs.getLong("payment_form_id")))
            .single();
    }
    
    public @NonNull Optional<Payment> deleteById(Long id) {
        String query =
            """
            DELETE FROM payments
            WHERE id = :id
            RETURNING id, amount, name, card_number, expiration,
                card_security_code, status_id, order_id, payment_form_id
            """;
        return jdbc.sql(query)
            .param("id", id)
            .query((rs, _) -> new Payment(
                rs.getLong("id"),
                BigInteger.valueOf(rs.getInt("amount")),
                rs.getString("name"),
                rs.getString("card_number"),
                rs.getString("expiration"),
                rs.getString("card_security_code"),
                Status.fromId(rs.getInt("status_id")),
                rs.getLong("order_id"),
                rs.getLong("payment_form_id")))
            .optional();
    }
}