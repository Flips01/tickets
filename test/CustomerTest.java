import org.junit.Test;

import static org.junit.Assert.*;

public class CustomerTest {
    @Test
    public void shouldCreate() {
        String name = "Olaf";
        String adress = "Street 1";
        Customer customer = new Customer(name, adress);
    }
}