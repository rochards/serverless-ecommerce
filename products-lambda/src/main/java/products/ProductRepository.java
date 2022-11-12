package products;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import java.util.Optional;

public class ProductRepository {

    private final DynamoDBMapper mapper;

    public ProductRepository() {
        this.mapper = new DynamoDBMapper(AmazonDynamoDBClientBuilder.standard().build());
    }

    public Optional<Product> findById(String id) {
        return Optional.ofNullable(mapper.load(Product.class, id));
    }
}
