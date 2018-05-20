import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Service {
    private List<Event> events = new ArrayList<>();

    public Customer createCustomer(String name, String address) {
        return new Customer(name, address);
    }

    public Event createEvent(String id, String title, Date date, int price, int seating) {
        Event event = new Event(id, title, date, price, seating);
        events.add(event);
        return event;
    }

    public List<Event> getEvents() {
        return new ArrayList<>(events);
    }
}
