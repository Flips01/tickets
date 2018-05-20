import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class CustomerTest {
    @Test
    public void shouldStoreName() {
        String name = "Olaf";

        Customer customer = new Customer(name, "");

        assertThat(customer.getName(), is(name));
    }

    @Test
    public void shouldStoreAddress() {
        String address = "Street 1";

        Customer customer = new Customer("", address);

        assertThat(customer.getAddress(), is(address));
    }
}