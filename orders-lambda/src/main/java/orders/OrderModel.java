package orders;

import java.math.BigDecimal;
import java.util.List;

public class OrderModel {

    private String email;
    private String orderId;
    private Shipping shipping;
    private Billing billing;
    private List<Product> products;

    private static class Shipping {
        private ShippingType type;
        private Carrier carrier;
    }

    private static class Billing {
        private PaymentMethod paymentMethod;
        private BigDecimal totalPrice;
    }

    public static class Product {
        private String id;
        private String code;
        private BigDecimal price;
    }
}
