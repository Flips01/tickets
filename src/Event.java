import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Event implements Serializable {
    private final String id;
    private final String title;
    private final Date date;
    private final int price;
    private final int seating;
}
