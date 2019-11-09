    package com.example.waghstrategy;


    import java.io.Serializable;

    public class Query implements Serializable {
    public Integer connectionKey;
    private static final long serialVersionUID = 3;
    public String nickname;
    public String operation;


    Query(Integer connectionKey,String nickname,String operation){
    this.connectionKey = connectionKey;
    this.nickname = nickname;
    this.operation = operation;
    }
    }


