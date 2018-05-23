import org.junit.Before;
import org.junit.Test;

import java.awt.print.Book;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class ServiceTest {
    private Service service;
    private Event defaultEvent;
    private Customer defaultCustomer;

    @Before
    public void setUp() {
        service = new Service();
        defaultEvent = createDefaultEvent();
        defaultCustomer = createDefaultCustomer();
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
    public void shouldShowAvailableSeats() throws Exception {
        insertDefaultEvent(service);

        assertThat(service.getAvailableSeats(defaultEvent), is(defaultEvent.getSeating()));
    }

    @Test
    public void shouldShowAvailableSeatsWithBooking() throws Exception {
        insertDefaultEvent(service);
        insertDefaultCustomer(service);

        Booking booking = service.createBooking(defaultCustomer, defaultEvent, 2);


        assertThat(service.getAvailableSeats(defaultEvent), is(defaultEvent.getSeating() - 2));
    }

    @Test
    public void shouldShowAvailableSeatsWithMultipleBooking() throws Exception {
        insertDefaultEvent(service);
        insertDefaultCustomer(service);

        service.createBooking(defaultCustomer, defaultEvent, 2);
        service.createBooking(defaultCustomer, defaultEvent, 2);

        assertThat(service.getAvailableSeats(defaultEvent), is(defaultEvent.getSeating() - 4));
    }

    @Test(expected = Exception.class)
    public void shouldShowAvailableSeatsNonExistingEvent() throws Exception {
        service.getAvailableSeats(defaultEvent);
    }

    @Test
    public void shouldListAllCustomers() {
        assertThat(service.getCustomers(), is(empty()));

        insertDefaultCustomer(service);

        assertThat(service.getCustomers(), contains(defaultCustomer));
    }

    @Test
    public void shouldAllowBookingAllSeats() throws Exception {
        insertDefaultCustomer(service);
        insertDefaultEvent(service);

        Booking booking = service.createBooking(defaultCustomer, defaultEvent, defaultEvent.getSeating());

        assertThat(booking, is(not(nullValue())));
        assertThat(service.getAvailableSeats(defaultEvent), is(0));
    }

    @Test
    public void shouldGetCustomerEventBooking() throws Exception {
        insertDefaultCustomer(service);
        insertDefaultEvent(service);

        Booking booking = service.createBooking(defaultCustomer, defaultEvent, defaultEvent.getSeating());

        Booking result = service.getCustomerBookingForEvent(defaultCustomer, defaultEvent);
        assertThat(result, is(booking));
    }

    @Test
    public void shouldGetCustomerEventBookingNull() throws Exception {
        insertDefaultCustomer(service);
        insertDefaultEvent(service);

        Booking result = service.getCustomerBookingForEvent(defaultCustomer, defaultEvent);
        assertThat(result, is(nullValue()));
    }

    @Test(expected = Exception.class)
    public void shouldFailWhenCreateBookingForNotRegisteredCustomer() throws Exception {
        insertDefaultEvent(service);

        service.createBooking(defaultCustomer, defaultEvent, defaultEvent.getSeating());
    }

    @Test(expected = Exception.class)
    public void shouldFailWhenCreateBookingForNotRegisteredEvent() throws Exception {
        insertDefaultCustomer(service);

        service.createBooking(defaultCustomer, defaultEvent, defaultEvent.getSeating());
    }

    @Test
    public void shouldGetCustomerEventBookingFromMultipleCustomers() throws Exception {
        insertDefaultCustomer(service);
        insertDefaultEvent(service);

        Booking firstBooking = service.createBooking(defaultCustomer, defaultEvent, 1);

        Customer customer = service.createCustomer("mueller", "strasse 123");
        Booking secondBooking = service.createBooking(customer, defaultEvent, 1);

        Booking result = service.getCustomerBookingForEvent(defaultCustomer, defaultEvent);
        assertThat(result, is(firstBooking));

        result = service.getCustomerBookingForEvent(customer, defaultEvent);
        assertThat(result, is(secondBooking));
    }

    @Test
    public void shouldGetCustomerEventBookingFromMultipleEvents() throws Exception {
        insertDefaultCustomer(service);
        insertDefaultEvent(service);

        Event secondEvent = service.createEvent("123", "test", Date.from(Instant.now()), 100, 100);

        Booking firstBooking = service.createBooking(defaultCustomer, defaultEvent, defaultEvent.getSeating());
        Booking secondBooking = service.createBooking(defaultCustomer, secondEvent, secondEvent.getSeating());


        Booking result = service.getCustomerBookingForEvent(defaultCustomer, defaultEvent);
        assertThat(result, is(firstBooking));

        result = service.getCustomerBookingForEvent(defaultCustomer, secondEvent);
        assertThat(result, is(secondBooking));
    }


    @Test(expected = Exception.class)
    public void shouldFailWhenGetBookingForNotRegisteredCustomer() throws Exception {
        insertDefaultEvent(service);

        service.getCustomerBookingForEvent(defaultCustomer, defaultEvent);
    }

    @Test(expected = Exception.class)
    public void shouldFailWhenGetBookingForNotRegisteredEvent() throws Exception {
        insertDefaultCustomer(service);

        service.getCustomerBookingForEvent(defaultCustomer, defaultEvent);
    }

    @Test
    public void shouldCombineBookings() throws Exception {
        insertDefaultCustomer(service);
        insertDefaultEvent(service);

        Booking firstBooking = service.createBooking(defaultCustomer, defaultEvent, 1);
        Booking secondBooking = service.createBooking(defaultCustomer, defaultEvent, 1);

        assertThat(firstBooking, is(not(secondBooking)));
        assertThat(secondBooking.getSeats(), is(2));
        assertThat(firstBooking.getId(), is(not(secondBooking.getId())));
    }

    @Test
    public void shouldRejectBookingWhenNoSeatsAreAvailable() throws Exception {
        insertDefaultCustomer(service);
        insertDefaultEvent(service);

        service.createBooking(defaultCustomer, defaultEvent, defaultEvent.getSeating());
        try {
            service.createBooking(defaultCustomer, defaultEvent, 1);
            fail();
        } catch (Exception ignored) {
        }
    }

    @Test
    public void shouldPersistService() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        insertDefaultCustomer(service);
        insertDefaultEvent(service);
        service.createBooking(defaultCustomer, defaultEvent, defaultEvent.getSeating());

        service.persist(outputStream);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        Service loadedService = Service.load(inputStream);

        assertThat(loadedService, is(service));
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

    private Customer createDefaultCustomer() {
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