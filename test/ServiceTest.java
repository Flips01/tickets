import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ServiceTest {
    private Service service;
    private Event defaultEvent;
    private Customer defaultCustomer;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

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
    public void shouldAlterAvailableSeatsAfterBooking() throws Exception {
        insertDefaultEvent(service);
        insertDefaultCustomer(service);

        service.createBooking(defaultCustomer, defaultEvent, 2);

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

        Event secondEvent = service.createEvent("123", "test", Date.from(Instant.now()), 100, 100, "");

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

        thrown.expect(Exception.class);
        service.createBooking(defaultCustomer, defaultEvent, 1);
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

    @Test
    public void shouldQueryBlacklist() throws Exception {
        BlackListService blackListService = mock(BlackListService.class);
        when(blackListService.isCustomerBlacklisted(defaultCustomer)).thenReturn(false);
        service.setBlacklistService(blackListService);
        insertDefaultCustomer(service);
        insertDefaultEvent(service);

        service.createBooking(defaultCustomer, defaultEvent, defaultEvent.getSeating());

        verify(blackListService).isCustomerBlacklisted(defaultCustomer);
    }

    @Test(expected = BlackListException.class)
    public void shouldFailOnBlacklist() throws Exception {
        BlackListService blackListService = mock(BlackListService.class);
        when(blackListService.isCustomerBlacklisted(defaultCustomer)).thenReturn(true);
        service.setBlacklistService(blackListService);
        insertDefaultCustomer(service);
        insertDefaultEvent(service);

        service.createBooking(defaultCustomer, defaultEvent, defaultEvent.getSeating());
    }

    @Test
    public void shouldSendEmailOnBookingsWithMoreThenTenPercentSeats() throws Exception {
        MailService mailService = mock(MailService.class);
        service.setMailService(mailService);
        insertDefaultCustomer(service);
        insertDefaultEvent(service);

        service.createBooking(defaultCustomer, defaultEvent, defaultEvent.getSeating());

        verify(mailService).sendMail(eq(defaultEvent.getOrganizerEmail()), anyString());
    }

    @Test
    public void shouldNotSendEmailOnBookingsWithLessThenTenPercentSeats() throws Exception {
        MailService mailService = mock(MailService.class);
        service.setMailService(mailService);
        insertDefaultCustomer(service);
        insertDefaultEvent(service);

        service.createBooking(defaultCustomer, defaultEvent, 1);

        verify(mailService, never()).sendMail(anyString(), anyString());
    }

    private Event createDefaultEvent() {
        String id = "1234";
        String title = "fun";
        Date date = Date.from(Instant.now());
        int price = 300;
        int seating = 100;
        String organizerEmail = "";

        return new Event(id, title, date, price, seating, organizerEmail);
    }

    private Event insertDefaultEvent(Service service) {
        return service.createEvent(
                defaultEvent.getId(),
                defaultEvent.getTitle(),
                defaultEvent.getDate(),
                defaultEvent.getPrice(),
                defaultEvent.getSeating(),
                defaultEvent.getOrganizerEmail()
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