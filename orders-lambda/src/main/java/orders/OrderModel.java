package orders;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConvertedEnum;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@DynamoDBTable(tableName = "OrdersTable") // nome como definido na classe OrdersAppStack no projeto do cdk
public class OrderModel {

    @DynamoDBIgnore
    public static final String HASH_KEY_ATTRIBUTE_NAME = "Email"; // conforme definido em OrdersAppStack
    @DynamoDBIgnore
    public static final String RANGE_KEY_ATTRIBUTE_NAME = "OrderId"; // conforme definido em OrdersAppStack

    @DynamoDBHashKey(attributeName = HASH_KEY_ATTRIBUTE_NAME)
    private String email;

    @DynamoDBAutoGeneratedKey
    @DynamoDBRangeKey(attributeName = RANGE_KEY_ATTRIBUTE_NAME)
    private String orderId;

    @DynamoDBRangeKey(attributeName = "CreatedAt")
    private long createdAt;

    @DynamoDBAttribute(attributeName = "Shipping")
    private Shipping shipping;

    @DynamoDBAttribute(attributeName = "Billing")
    private Billing billing;

    @DynamoDBAttribute(attributeName = "Products")
    private List<Product> products;

    @DynamoDBIgnore
    @Override
    public String toString() {
        return "OrderModel{" +
                "email='" + email + '\'' +
                ", orderId='" + orderId + '\'' +
                ", createdAt=" + createdAt +
                ", shipping=" + shipping +
                ", billing=" + billing +
                ", products=" + products +
                '}';
    }

    @Getter
    @Setter
    @DynamoDBDocument
    public static class Shipping {

        @DynamoDBTypeConvertedEnum
        @DynamoDBAttribute(attributeName = "Type")
        private ShippingType type;

        @DynamoDBTypeConvertedEnum
        @DynamoDBAttribute(attributeName = "Carrier")
        private Carrier carrier;

        @DynamoDBIgnore
        @Override
        public String toString() {
            return "Shipping{" +
                    "type=" + type +
                    ", carrier=" + carrier +
                    '}';
        }
    }

    @Getter
    @Setter
    @DynamoDBDocument
    public static class Billing {

        @DynamoDBTypeConvertedEnum
        @DynamoDBAttribute(attributeName = "PaymentMethod")
        private PaymentMethod paymentMethod;

        @DynamoDBAttribute(attributeName = "TotalPrice")
        private BigDecimal totalPrice;

        @DynamoDBIgnore
        @Override
        public String toString() {
            return "Billing{" +
                    "paymentMethod=" + paymentMethod +
                    ", totalPrice=" + totalPrice +
                    '}';
        }
    }

    @Getter
    @Setter
    @DynamoDBDocument
    public static class Product {

        @DynamoDBAttribute(attributeName = "Id")
        private String id;

        @DynamoDBAttribute(attributeName = "Code")
        private String code;

        @DynamoDBAttribute(attributeName = "Price")
        private BigDecimal price;

        @DynamoDBIgnore
        @Override
        public String toString() {
            return "Product{" +
                    "id='" + id + '\'' +
                    ", code='" + code + '\'' +
                    ", price=" + price +
                    '}';
        }
    }
}
