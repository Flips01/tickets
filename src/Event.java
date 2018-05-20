import lombok.Data;

import java.util.Date;

@Data
public class Event {
    private final String id;
    private final String title;
    private final Date date;
    private final int price;
    private final int seating;
}
