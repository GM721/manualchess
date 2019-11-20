package CommonClasses;

import java.io.Serializable;

public class CollocutorQuery extends Query implements Serializable {
    public String collocutor;
    CollocutorQuery(Integer connectionKey,String nickname,String collocutor){
        super (connectionKey,nickname, "collocation");
        this.collocutor=collocutor;
    }
    CollocutorQuery(Integer connectionKey,String nickname,String operation,String collocutor){
        super (connectionKey,nickname, operation);
        this.collocutor=collocutor;
    }
}
