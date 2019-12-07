package CommonClasses;

import java.util.ArrayList;

public class MessagesAnswer extends Answer {
    private static final long serialVersionUID = 11;

    public ArrayList<Message> messages;

    public MessagesAnswer(Boolean isOk, String operation, String description) {
        super(isOk, operation, description);
    }
}
