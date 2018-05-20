import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class ServiceTest {
    private Service service;
    private Event defaultEvent;
    private Customer defaultCustomer;

    @Before
    public void setUp() {
        service = new Service();
        defaultEvent = createDefaultEvent();
        defaultCustomer = createDefaultCustomert();
    }

    @Test
    public void shouldCreateCustomer() {
        Customer insertedCustomer = insertDefaultCustomer(service);

        assertThat(insertedCustomer, is(defaultCustomer));
    }

    @Test
    public void shouldCreateEvent() {
        Event insertedEvent = insertDefaultEvent(service);
        
        assertThat(insertedEvent, is(defaultEvent));
    }

    @Test
    public void shouldListAllEvents() {
        assertThat(service.getEvents(), is(empty()));

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

    @Test
    public void shouldListAllCustomers() {
        assertThat(service.getCustomers(), is(empty()));

        insertDefaultCustomer(service);

        assertThat(service.getCustomers(), contains(defaultCustomer));
    }

    @Test
    public void shouldAllowBookingAllSeats() {
        insertDefaultCustomer(service);
        insertDefaultEvent(service);

        Booking booking = service.createBooking(defaultCustomer, defaultEvent, defaultEvent.getSeating());

        assertThat(booking, is(not(nullValue())));
        assertThat(service.getAvailableSeats(defaultEvent), is(0));
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

    private Customer createDefaultCustomert() {
        String name = "Olaf";
        String address = "Street 1";

        return new Customer(name, address);
    }

    private Customer insertDefaultCustomer(Service service) {
        return service.createCustomer(
                defaultCustomer.getName(),
                defaultCustomer.getAddress()
        );
    }
}