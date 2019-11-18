package com.company.CommnClasses;

import java.io.Serializable;

public class PlayerAuthorise extends Query implements Serializable {
    private static final long serialVersionUID = 4;
    public String password;

    PlayerAuthorise(Boolean isNew, String nickname, String password,
                    Integer connectionKey, String operation){
        super(connectionKey,nickname,operation);
        connectionKey = null;
        this.password = password;
    }
}