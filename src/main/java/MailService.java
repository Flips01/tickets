import java.io.Serializable;

public interface MailService extends Serializable {
    void sendMail(String address, String message);
}
