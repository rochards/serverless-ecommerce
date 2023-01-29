package product;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@DynamoDBTable(tableName = ProductModel.PRODUCTS_TABLE_NAME)
public class ProductModel {

    @DynamoDBIgnore
    public static final String PRODUCTS_TABLE_NAME = "ProductsTable";

    @DynamoDBHashKey(attributeName = "Id")
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
