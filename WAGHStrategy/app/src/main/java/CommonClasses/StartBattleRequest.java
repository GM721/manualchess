package CommonClasses;

public class StartBattleRequest extends CollocutorAnswer {
    private static final long serialVersionUID = 15;
    public StartBattleRequest(Boolean isOk, String operation, String description, String collocutor) {
        super(isOk, operation, description, collocutor);
    }
}
