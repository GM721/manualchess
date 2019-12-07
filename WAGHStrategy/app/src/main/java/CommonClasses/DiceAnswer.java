package CommonClasses;

import java.io.Serializable;

public class DiceAnswer extends CollocutorAnswer implements Serializable {
    public int userResult;
    public int collocutorResult;
    private static final long serialVersionUID = 16;
    public DiceAnswer(Boolean isOk, String operation, String description, String collocutor) {
        super(isOk, operation, description, collocutor);
    }
}
