package CommonClasses;

import java.io.Serializable;

public class PlayerRegister extends Query implements Serializable {
    private static final long serialVersionUID = 3;
    public String password;
    public String email;

    public PlayerRegister(String nickname, String email, String password){
        super(null, nickname,"registration");
        this.password = password;
        this.email = email;
    }
    public Query getRealClass()
    {
        return this;
    }
}
