package CommonClasses;

import java.io.Serializable;

public class CollocutorAnswer extends Answer implements Serializable {
    public String collocutor;
    CollocutorAnswer(Boolean isOk, String description,String collocutor)
    {
        super(isOk,"collocation",description);
        this.collocutor=collocutor;
    }
    CollocutorAnswer(Boolean isOk,String operation, String description,String collocutor)
    {
        super(isOk,operation,description);
        this.collocutor=collocutor;
    }
}
