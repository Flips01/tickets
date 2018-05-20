import org.junit.Test;

import java.time.Instant;
import java.util.Date;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.*;

public class ServiceTest {
    @Test
    public void shouldCreateCustomer() {
        Service service = new Service();
        String name = "Olaf";
        String address = "Street 1";
        Customer expectedCustomer = new Customer(name, address);

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

    @Test
    public void shouldListAllEvents() {
        Service service = new Service();
        String id = "1234";
        String title = "fun";
        Date date = Date.from(Instant.now());
        int price = 300;
        int seating = 100;
        Event expectedEvent = new Event(id, title, date, price, seating);

        service.createEvent(id, title, date, price, seating);

        assertThat(service.getEvents(), contains(expectedEvent));
    }

    @Test
    public void shouldShowAvailableSeats() {
        Service service = new Service();
        String id = "1234";
        String title = "fun";
        Date date = Date.from(Instant.now());
        int price = 300;
        int seating = 100;

        Event event = service.createEvent(id, title, date, price, seating);

        assertThat(service.getAvailableSeats(event), is(seating));
    }

    @Test
    public void shouldShowAvailableSeatsNullEvent() {
        Service service = new Service();

        assertThat(service.getAvailableSeats(null), is(nullValue()));
    }

    @Test
    public void shouldShowAvailableSeatsNonExistingEvent() {
        Service service = new Service();
        String id = "1234";
        String title = "fun";
        Date date = Date.from(Instant.now());
        int price = 300;
        int seating = 100;
        Event event = new Event(id, title, date, price, seating);

        assertThat(service.getAvailableSeats(event), is(nullValue()));
    }
}