package com.rochards.orders;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Billing {
    private PaymentMethod paymentMethod;
    private BigDecimal totalPrice;
}
