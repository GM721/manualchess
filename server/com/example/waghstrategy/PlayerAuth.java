package com.example.waghstrategy;


import java.io.Serializable;

public class PlayerAuth extends Query implements Serializable {
private static final long serialVersionUID = 2;
public String email;
public String password;

PlayerAuth(Boolean isNew, String nickname, String email, String password,
Integer connectionKey, String operation, String source){
super(connectionKey,nickname,operation);
this.email = email;
this.password = password;
}
}
