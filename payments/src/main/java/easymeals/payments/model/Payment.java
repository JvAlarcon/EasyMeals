package easymeals.payments.model;

import java.math.BigInteger;

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
) {}