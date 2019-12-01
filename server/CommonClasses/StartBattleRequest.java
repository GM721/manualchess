package CommonClasses;

import java.io.Serializable;

public class StartBattleRequest extends CollocutorAnswer implements Serializable
{
	public StartBattleRequest(Boolean isOk, String description, String collocutor)
	{
		super(isOk, "startBattle", description);
		this.collocutor = collocutor;
	}
}
