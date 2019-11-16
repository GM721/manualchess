package com.example.waghstrategy;

import java.io.Serializable;

public class PlayerRegister extends Query implements Serializable {
    private static final long serialVersionUID = 3;
    public String email;
    public String password;

    PlayerRegister(String nickname, String email, String password,
                   Integer connectionKey, String operation){
        super(connectionKey,nickname,operation);
        this.email = email;
        this.password = password;
    }

    public Query getRealClass(){
        return this;
    }
}
