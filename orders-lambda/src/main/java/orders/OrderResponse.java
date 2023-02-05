package orders;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@ToString
public class OrderResponse {

    private String id;
    private String email;
    private long createdAt;
    private Billing billing;
    private Shipping shipping;
    private List<Product> products;

    @Getter
    @Setter
    @ToString
    private static class Billing {
        private PaymentMethod paymentMethod;
        private BigDecimal totalPrice;
    }

    @Getter
    @Setter
    @ToString
    private static class Product {
        private String id;
        private String code;
        private BigDecimal price;
    }
}
