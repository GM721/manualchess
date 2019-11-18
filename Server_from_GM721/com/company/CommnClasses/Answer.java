package com.company.CommnClasses;

import java.io.Serializable;

public class Answer implements Serializable{
    private static final long serialVersionUID = 1;
    public Boolean isOk;
    public String operation;
    public String description;

    public Answer(Boolean isOk, String operation, String description){
        this.isOk = isOk;
        this.operation = operation;
        this.description = description;
    }
}