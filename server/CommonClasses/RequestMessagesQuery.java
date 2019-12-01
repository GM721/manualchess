package CommonClasses;

public class RequestMessagesQuery extends Query {
    private static final long serialVersionUID = 12;
     public Calendar date;

    public RequestMessagesQuery(Integer connectionKey, String nickname,Calendar date) {
        super(connectionKey, nickname, "startGettingMessages");
        this.date = date;
    }
}
