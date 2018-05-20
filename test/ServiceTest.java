import org.junit.Test;

import java.time.Instant;
import java.util.Date;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class ServiceTest {
    @Test
    public void shouldCreateCustomer() {
        Service service = new Service();
        String name = "Olaf";
        String address = "Street 1";
        Customer expectedCustomer = new Customer(name,address);

        Customer customer = service.createCustomer(name, address);

        assertThat(customer, is(expectedCustomer));
    }

    @Test
    public void shouldCreateEvent() {
        Service service = new Service();
        String id = "1234";
        String title = "fun";
        Date date = Date.from(Instant.now());
        int price = 300;
        int seating = 100;
        Event expectedEvent = new Event(id, title, date, price, seating);

        Event event = service.createEvent(id, title, date, price, seating);

        assertThat(event, is(expectedEvent));
    }
}