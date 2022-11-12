package products;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ProductRepository {

    private static final Logger LOGGER = LogManager.getLogger(ProductRepository.class);

    private final DynamoDBMapper mapper;

    public ProductRepository() {
        this.mapper = new DynamoDBMapper(AmazonDynamoDBClientBuilder.standard().build());
    }

    public Product save(Product product) {
        var id = UUID.randomUUID().toString();
        product.setId(id);

        mapper.save(product);

        return product;
    }

    public Optional<Product> delete(String id) {
        // daria para usar um deleteExpression, semelhante ao que foi feito no metodo update

        var optionalProduct = this.findById(id);

        optionalProduct.ifPresent(mapper::delete);

        return optionalProduct;
    }

    private Optional<Product> update(String id, Product updatedProduct) {
        var saveExpression = new DynamoDBSaveExpression();
        saveExpression.setExpected(
                Map.of(Product.HASH_KEY_ATTRIBUTE_NAME, new ExpectedAttributeValue(true))
        );

        try {
            updatedProduct.setId(id);
            mapper.save(updatedProduct, saveExpression);

            return Optional.of(updatedProduct);
        } catch (ConditionalCheckFailedException e) {
            LOGGER.error("Failed to update product: " + updatedProduct, e);
            return Optional.empty();
        }


    }

    private Optional<Product> findById(String id) {
        return Optional.ofNullable(mapper.load(Product.class, id));
    }
}
