package CommonClasses;

import java.io.Serializable;

public class AuthorisationAnswer extends Answer implements Serializable {
	private static final long serialVersionUID = 5;
    public String nickname;
    public Integer connectionKey;
    public AuthorisationAnswer(Boolean isOk, String nickname, Integer connectionKey, String operation, String discription){
        super(isOk,operation,discription);
        this.nickname=nickname;
        this.connectionKey=connectionKey;
    }
}
