package CommonClasses;

public class CollocutorAnswer extends Answer {
    private static final long serialVersionUID = 6;
    public String collocutor;
    public CollocutorAnswer(Boolean isOk, String operation, String description,String collocutor) {
        super(isOk, operation, description);
        this.collocutor = collocutor;
    }
}
