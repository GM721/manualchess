package com.example.waghstrategy;

import java.io.Serializable;

public class Answer implements Serializable {
    private static final long serialVersionUID = 1;
    Boolean isOk;
    String name;
    Integer connectionKey;
    String operation;
    String description;

    Answer(Boolean isOk,String name, Integer connectionKey,String operation,String description){
        this.isOk = isOk;
        this.name = name;
        this.connectionKey = connectionKey;
        this.operation = operation;
        this.description = description;
    }
}
