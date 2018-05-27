import java.io.Serializable;

public interface BlacklistService extends Serializable {
    boolean isCustomerBlacklisted(Customer customer);
}
