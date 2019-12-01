package CommonClasses;

import java.io.Serializable;

public class MessageAnswer extends Answer implements Serializable {
	static final long serialVersionUID = 9;
    public String sender;
    public String receiver;
    public String message;
    public Calendar date;
    MessageAnswer(Boolean isOk, String description, String sender, String receiver , String message, Calendar date){
        super(isOk,"message", description);
        this.sender=sender;
        this.receiver=receiver;
        this.message=message;
        this.date=date;
    }
    public MessageAnswer(Boolean isOk, String description, MessageQuery mq, Calendar date)
    {
        super(isOk,"message",description);
        this.sender=mq.nickname;
        this.receiver=mq.collocutor;
        this.message=mq.message;
        this.date=date;
    }
}
