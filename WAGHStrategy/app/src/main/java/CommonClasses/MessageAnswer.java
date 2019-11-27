package CommonClasses;

import java.util.Calendar;
import java.util.Date;

public class MessageAnswer extends Answer {
    public String sender;
    public String receiver;
    public String message;
    public Calendar date;
    public MessageAnswer(Boolean isOk, String operation, String description,String sender, String receiver,
    String message,Calendar date) {
        super(isOk, operation, description);
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.date = date;
    }
}
