package CommonClasses;

public class CollocutorQuery extends Query {
    private static final long serialVersionUID = 7;
    String collocutor;
    CollocutorQuery(Integer connectionKey, String nickname, String operation, String collocutor){
        super(connectionKey,nickname,operation);
        this.collocutor = collocutor;
    }
}
