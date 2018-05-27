import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@ToString
@EqualsAndHashCode
public class Service implements Serializable {
    private final List<Event> events = new ArrayList<>();
    private final List<Customer> customers = new ArrayList<>();
    private final List<Booking> bookings = new ArrayList<>();

    @Setter
    @Getter
    private BlacklistService blacklistService;

    @Setter
    @Getter
    private MailService mailService;

    public static Service load(InputStream inputStream) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(inputStream)) {
            return (Service) ois.readObject();
        }
    }

    public Customer registerCustomer(String name, String address) {
        Customer customer = new Customer(name, address);
        customers.add(customer);
        return customer;
    }

    public Event registerEvent(String id, String title, Date date, int price, int seating, String organizerEmail) {
        Event event = new Event(id, title, date, price, seating, organizerEmail);
        events.add(event);
        return event;
    }

    public List<Event> getEvents() {
        return new ArrayList<>(events);
    }

    public Integer getAvailableSeats(Event event) throws Exception {
        if (isNotRegistered(event)) {
            throw new Exception();
        }

        int usedSeats = bookings
                .stream()
                .filter(booking -> booking.getEvent().equals(event))
                .mapToInt(Booking::getSeats)
                .sum();

        return event.getSeating() - usedSeats;
    }

    public List<Customer> getCustomers() {
        return new ArrayList<>(customers);
    }

    private boolean isNotRegistered(Customer customer) {
        return customers.stream().noneMatch(customer::equals);
    }

    private boolean isNotRegistered(Event event) {
        return events.stream().noneMatch(event::equals);

    }

    public Booking createBooking(Customer customer, Event event, int requestedSeats) throws Exception {
        if (isNotRegistered(customer) || isNotRegistered(event)) {
            throw new Exception();
        }

        if (getAvailableSeats(event) < requestedSeats) {
            throw new Exception();
        }

        if (blacklistService != null && blacklistService.isCustomerBlacklisted(customer)) {
            throw new BlackListException();
        }

        if (mailService != null && requestedSeats >= event.getSeating() * 0.1) {
            mailService.sendMail(event.getOrganizerEmail(), "");
        }

        Booking oldBooking = getCustomerBookingForEvent(customer, event);
        int usedSeats = requestedSeats;
        if (oldBooking != null) {
            usedSeats += oldBooking.getSeats();
            bookings.remove(oldBooking);
        }

        Booking booking = new Booking(customer, event, usedSeats, String.valueOf(ThreadLocalRandom.current().nextInt(0, 9999)));
        bookings.add(booking);
        return booking;
    }

    public Booking getCustomerBookingForEvent(Customer customer, Event event) throws Exception {
        if (isNotRegistered(customer) || isNotRegistered(event)) {
            throw new Exception();
        }

        return bookings
                .stream()
                .filter(booking -> booking.getEvent().equals(event) && booking.getCustomer().equals(customer))
                .findFirst()
                .orElse(null);
    }

    public void persist(OutputStream outputStream) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(outputStream)) {
            oos.writeObject(this);
        }
    }
}
