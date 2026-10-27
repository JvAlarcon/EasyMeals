package easymeals.payments.model;

import java.math.BigInteger;
import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "payments")
public record Payment(
    @Id Long id,
    BigInteger amount,
    String name,
    String cardNumber,
    String expiration,
    String cardSecurityCode,
    Status status,
    Long orderId,
    Long paymentFormId
) {
    public Payment {
        Objects.requireNonNull(id);
        Objects.requireNonNull(amount);
        Objects.requireNonNull(name);
        Objects.requireNonNull(cardNumber);
        Objects.requireNonNull(expiration);
        Objects.requireNonNull(cardSecurityCode);
        Objects.requireNonNull(status);
        Objects.requireNonNull(orderId);
        Objects.requireNonNull(paymentFormId);
    }
    
    public record NewPayment(
        BigInteger amount,
        String name,
        String cardNumber,
        String expiration,
        String cardSecurityCode,
        Status status,
        Long orderId,
        Long paymentFormId
    ) {
        public NewPayment {
            Objects.requireNonNull(amount);
            Objects.requireNonNull(name);
            Objects.requireNonNull(cardNumber);
            Objects.requireNonNull(expiration);
            Objects.requireNonNull(cardSecurityCode);
            Objects.requireNonNull(status);
            Objects.requireNonNull(orderId);
            Objects.requireNonNull(paymentFormId);
        }
    }
}