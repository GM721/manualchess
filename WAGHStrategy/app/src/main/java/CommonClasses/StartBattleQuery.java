package CommonClasses;

import java.io.Serializable;

public class StartBattleQuery extends CollocutorQuery implements Serializable {
    public boolean isReady;
    private static final long serialVersionUID = 14;
    public StartBattleQuery(Integer connectionKey, String nickname, String collocutor,Boolean isReady,String operation) {
        super(connectionKey, nickname, collocutor);
        this.isReady = isReady;
        this.operation=operation;
    }
}
