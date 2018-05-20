import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

public class ServiceTest {
    private Service service;
    private Event defaultEvent;

    @Before
    public void setUp() {
        service = new Service();
        defaultEvent = createDefaultEvent();
    }

    @Test
    public void shouldCreateCustomer() {
        String name = "Olaf";
        String address = "Street 1";
        Customer expectedCustomer = new Customer(name, address);

        Customer customer = service.createCustomer(name, address);

        assertThat(customer, is(expectedCustomer));
    }

    @Test
    public void shouldCreateEvent() {
        Event insertedEvent = insertDefaultEvent(service);
        
        assertThat(insertedEvent, is(defaultEvent));
    }

    @Test
    public void shouldListAllEvents() {
        insertDefaultEvent(service);

        assertThat(service.getEvents(), contains(defaultEvent));
    }

    @Test
    public void shouldShowAvailableSeats() {
        Event event = insertDefaultEvent(service);

        assertThat(service.getAvailableSeats(event), is(defaultEvent.getSeating()));
    }

    @Test
    public void shouldShowAvailableSeatsNullEvent() {
        Service service = new Service();

        assertThat(service.getAvailableSeats(null), is(nullValue()));
    }

    @Test
    public void shouldShowAvailableSeatsNonExistingEvent() {
        Service service = new Service();

        assertThat(service.getAvailableSeats(defaultEvent), is(nullValue()));
    }

    private Event createDefaultEvent() {
        String id = "1234";
        String title = "fun";
        Date date = Date.from(Instant.now());
        int price = 300;
        int seating = 100;
        return new Event(id, title, date, price, seating);
    }

    private Event insertDefaultEvent(Service service) {
        return service.createEvent(
                defaultEvent.getId(),
                defaultEvent.getTitle(),
                defaultEvent.getDate(),
                defaultEvent.getPrice(),
                defaultEvent.getSeating()
        );
    }
}