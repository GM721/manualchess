package CommonClasses;

import java.io.Serializable;

public class StartBattleRequest extends CollocutorAnswer implements Serializable
{
	static final long serialVersionUID = 15;
	public StartBattleRequest(Boolean isOk, String description, String collocutor)
	{
		super(isOk, "startBattle", description, collocutor);
	}
}
