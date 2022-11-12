package products;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.math.BigDecimal;

@DynamoDBTable(tableName = "ProductsTable") // nome como definido na classe ProductsAppStack no projeto do cdk
public class Product {

    // ATENCAO pois o atributo abaixo precisa ter o mesmo nome da Partition Key definida na classe ProductsAppStack no projeto do cdk
    @DynamoDBHashKey(attributeName = "Id")
    private String id;

    @DynamoDBAttribute(attributeName = "Name")
    private String name;

    @DynamoDBAttribute(attributeName = "Code")
    private String code;

    @DynamoDBAttribute(attributeName = "Model")
    private String model;

    @DynamoDBAttribute(attributeName = "Price")
    private BigDecimal price;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
