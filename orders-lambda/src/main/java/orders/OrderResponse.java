package orders;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Billing {
        private PaymentMethod paymentMethod;
        private BigDecimal totalPrice;
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Product {
        private String id;
        private String code;
        private BigDecimal price;
    }
}
