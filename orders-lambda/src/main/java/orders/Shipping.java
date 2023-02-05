package orders;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
class Shipping {
    private ShippingType type;
    private Carrier carrier;
}
