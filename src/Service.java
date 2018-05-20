import java.util.*;

public class Service {
    private List<Event> events = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private Map<Event, Integer> availableSeats = new HashMap<>();

    public Customer createCustomer(String name, String address) {
        Customer customer = new Customer(name, address);
        customers.add(customer);
        return customer;
    }

    public Event createEvent(String id, String title, Date date, int price, int seating) {
        Event event = new Event(id, title, date, price, seating);
        events.add(event);
        availableSeats.put(event, event.getSeating());
        return event;
    }

    public List<Event> getEvents() {
        return new ArrayList<>(events);
    }

    public Integer getAvailableSeats(Event event) {
        return availableSeats.get(event);
    }

    public List<Customer> getCustomers() {
        return new ArrayList<>(customers);
    }
}
