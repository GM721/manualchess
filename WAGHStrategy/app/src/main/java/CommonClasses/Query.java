package CommonClasses;

import java.io.Serializable;

public class Query implements Serializable {
    private static final long serialVersionUID = 2;
    public Integer connectionKey;
    public String nickname;
    public String operation;


    public Query(Integer connectionKey, String nickname, String operation){
        this.connectionKey = connectionKey;
        this.nickname = nickname;
        this.operation = operation;
    }
}