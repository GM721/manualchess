package CommonClasses;

import java.util.Calendar;
import java.util.Date;

public class MessageAnswer extends Answer {
    private static final long serialVersionUID = 9;
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
