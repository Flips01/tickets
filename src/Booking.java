import lombok.Data;

import java.io.Serializable;

@Data
public class Booking implements Serializable {
    private final Customer customer;
    private final Event event;
    private final int seats;
    private final String id;
}
