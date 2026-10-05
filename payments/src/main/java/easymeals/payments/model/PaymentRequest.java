package easymeals.payments.model;

import java.math.BigInteger;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record PaymentRequest (

    @NotNull(message = "{payment.amount.required}")
    @Positive(message = "{payment.amount.positive}")
    BigInteger amount,
    
    @NotBlank(message = "{payment.name.required}")
    @Size(max=100, message = "{payment.name.length}")
    String name,
    
    @NotBlank(message = "{payment.cardNumber.required}")
    @Size(max = 20, message = "{payment.cardNumber.length}")
    String cardNumber,
    
    @NotBlank(message = "{payment.expiration.required}")
    @Size(max = 7, message = "{payment.expiration.length}")
    String expiration,
    
    @NotBlank(message = "{payment.cardSecurityCode.required}")
    @Size(min = 3, max = 3, message = "{payment.cardSecurityCode.length}")
    String cardSecurityCode,
    
    @NotNull(message = "{payment.status.required}")
    Integer statusId,
    
    @NotNull(message = "{payment.orderId.required}")
    Long orderId,
    
    @NotNull(message = "{payment.paymentFormId.required}")
    Long paymentFormId
) {}