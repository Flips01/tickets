import java.io.Serializable;

public interface BlackListService extends Serializable {
    boolean isCustomerBlacklisted(Customer customer);
}
