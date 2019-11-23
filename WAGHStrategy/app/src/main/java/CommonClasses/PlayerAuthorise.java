package CommonClasses;

import java.io.Serializable;

public class PlayerAuthorise extends Query implements Serializable {
    private static final long serialVersionUID = 4;
    public String password;

    public PlayerAuthorise(Integer connectionKey, String nickname,String password, String operation) {
        super(connectionKey, nickname, operation);
        connectionKey=null;
        this.password = password;
    }

    public Query getRealClass(){
        return this;
    }
}
