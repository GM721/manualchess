package CommonClasses;

import java.util.Date;

public class MessageAnswer extends Answer {
    String sender;
    String receiver;
    String message;
    Date date;
    public MessageAnswer(Boolean isOk, String operation, String description,String sender, String receiver,
    String message,Date date) {
        super(isOk, operation, description);
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.date = date;
    }
}
