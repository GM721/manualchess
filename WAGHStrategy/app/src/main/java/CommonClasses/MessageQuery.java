package CommonClasses;

public class MessageQuery extends CollocutorQuery {
    String message;
    MessageQuery(Integer connectionKey, String nickname, String operation, String collocutor, String message) {
        super(connectionKey, nickname, operation, collocutor);
        this.message = message;
    }
}
