package CommonClasses;

import java.io.Serializable;
import java.util.Date;

public class MessageAnswer extends CollocutorAnswer implements Serializable {
    public String sender;
    public String receiver;
    public String message;
    public Date date;
    MessageAnswer(Boolean isOk, String description,String collocutor, String sender, String receiver , String message, Date date){
        super(isOk,"message", description, collocutor);
        this.sender=sender;
        this.receiver=receiver;
        this.message=message;
        this.date=date;
    }
    public MessageAnswer(Boolean isOk, String description, MessageQuery mq, Date date)
    {
        super(isOk,"message",description,mq.collocutor);
        this.sender=mq.nickname;
        this.receiver=mq.collocutor;
        this.message=mq.message;
        this.date=date;
    }
}
