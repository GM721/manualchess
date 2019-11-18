package com.company.CommnClasses;

import java.io.Serializable;
import java.util.Date;

public class MessageAnswer extends CollocutorAnswer implements Serializable {
    public String sender;
    public String receiver;
    public String message;
    public Date date;
    MessageAnswer(Boolean isOk, String discription,String collocutor, String sender, String receiver , String message, Date date){
        super(isOk,"message",discription,collocutor);
        this.sender=sender;
        this.receiver=receiver;
        this.message=message;
        this.date=date;
    }
    public MessageAnswer(Boolean isOk, String discription, MessageQuery mq, Date date)
    {
        super(isOk,"message",discription,mq.collocutor);
        this.sender=mq.nickname;
        this.receiver=mq.collocutor;
        this.message=mq.message;
        this.date=date;
    }

}
