package product;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProductRepository {
    private static final Logger LOGGER = LogManager.getLogger(ProductRepository.class);

    private final DynamoDBMapper mapper;

    public ProductRepository() {
        this.mapper = new DynamoDBMapper(AmazonDynamoDBClientBuilder.standard().build());
    }

    public List<ProductModel> findByIds(List<String> ids) {
        List<ProductModel> products = ids.stream()
                .map(id -> {
                    var product = new ProductModel();
                    product.setId(id);
                    return product;
                })
                .collect(Collectors.toList());

        LOGGER.info("Batch querying for products: {}", products);

        Map<String, List<Object>> mappedProducts = mapper.batchLoad(products);

        products = mappedProducts.get(ProductModel.PRODUCTS_TABLE_NAME).stream()
                .map(object -> (ProductModel) object)
                .collect(Collectors.toList());

        LOGGER.info("Found products: {}", products);
        return products;
    }
}
