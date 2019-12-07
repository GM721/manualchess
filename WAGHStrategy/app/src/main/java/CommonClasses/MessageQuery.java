package CommonClasses;

public class MessageQuery extends CollocutorQuery {
    private static final long serialVersionUID = 10;
    String message;
    public MessageQuery(Integer connectionKey, String nickname, String collocutor, String message) {
        super(connectionKey, nickname,  collocutor);
        this.message = message;
        this.operation = "message";
    }
}
