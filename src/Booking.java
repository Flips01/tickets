import lombok.Data;

@Data
public class Booking {
    private final Customer customer;
    private final Event event;
    private final int seats;
    private final String id;
}
