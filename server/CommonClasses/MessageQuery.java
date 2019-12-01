package CommonClasses;

import java.io.Serializable;

public class MessageQuery extends CollocutorQuery implements Serializable {
    public String message;
    static final long serialVersionUID = 10;
    public MessageQuery(Integer connectionKey, String nickname, String receiver, String message)
    {
        super(connectionKey, nickname, "message",receiver);
        this.message=message;
    }
}
