package com.rochards.orders;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class OrderRequest {
    private String email;
    private List<String> productsIds;
    private PaymentMethod paymentMethod;
    private Shipping shipping;

}
