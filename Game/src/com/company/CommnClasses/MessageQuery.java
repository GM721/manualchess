package com.company.CommnClasses;

import java.io.Serializable;

public class MessageQuery extends CollocutorQuery implements Serializable {
    public String message;
    public MessageQuery(Integer connectionKey, String nickname, String receiver, String message)
    {
        super(connectionKey, nickname, "message",receiver);
        this.message=message;
    }
}