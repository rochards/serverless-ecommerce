package products;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class ProductEventRequest {

    private String requestId;
    private EventType eventType;
    private String productId;
    private String productCode;
    private BigDecimal productPrice;
    private String email;
}
