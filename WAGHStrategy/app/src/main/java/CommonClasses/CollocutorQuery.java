package CommonClasses;

public class CollocutorQuery extends Query {
    private static final long serialVersionUID = 7;
    String collocutor;
    public CollocutorQuery(Integer connectionKey, String nickname, String collocutor){
        super(connectionKey,nickname,"collocation");
        this.collocutor = collocutor;
    }
}
