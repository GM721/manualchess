package com.company.CommnClasses;

import java.io.Serializable;

public class CollocutorAnswer extends Answer implements Serializable {
    public String collocutor;
    CollocutorAnswer(Boolean isOk, String discription,String collocutor)
    {
        super(isOk,"collocation",discription);
        this.collocutor=collocutor;
    }
    CollocutorAnswer(Boolean isOk,String operation, String discription,String collocutor)
    {
        super(isOk,operation,discription);
        this.collocutor=collocutor;
    }
}
