package CommonClasses;

import java.io.Serializable;

public class DiceAnswer extends CollocutorAnswer implements Serializable
{
	private static final long serialVersionUID = 16;
	int userResult;
	int collocutorResult;
	public DiceAnswer(Boolean isOk, String description, String opponent, int first, int second)
	{
		super(isOk, "diceRoll", description, opponent);
		this.userResult = first;
		this.collocutorResult = second;
	}
}
