import java.util.Date;

public class Service {
    public Customer createCustomer(String name, String address) {
        return new Customer(name, address);
    }

    public Event createEvent(String id, String title, Date date, int price, int seating) {
        return new Event(id, title, date, price, seating);
    }
}
