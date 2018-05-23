import lombok.Data;

import java.io.Serializable;

@Data
public class Customer implements Serializable {
    private final String name;
    private final String address;
}
