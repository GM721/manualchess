package CommonClasses;


import java.io.Serializable;

public class PlayerRegister extends Query implements Serializable {
	private static final long serialVersionUID = 3;
	public String password;
	public String email;

	PlayerRegister(String nickname, String email, String password, Integer connectionKey, String operation){
		super(connectionKey, operation, nickname);
		this.password = password;
		this.email = email;
	}
	public Query getRealClass()
	{
		return this;
	}
}