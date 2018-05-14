import org.junit.Test;

import java.time.Instant;
import java.util.Date;

import static org.junit.Assert.*;

public class EventTest {
    @Test
    public void shouldCreate() {
        String id = "1234";
        String title = "fun";
        Date date = Date.from(Instant.now());
        int price = 300;
        int seating = 100;
        Event event = new Event(id, title, date, price, seating);
    }
}