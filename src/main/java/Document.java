import java.util.HashMap;

public class Document {
    private String primary;
    private HashMap<String, Integer> termPosition;

    public Document(String primary) {
        this.primary = primary;
    }

    public String getPrimary() {
        return primary;
    }

    public HashMap<String, Integer> getTermPosition() {
        return termPosition;
    }

    public void setTermPosition(HashMap<String, Integer> termPosition) {
        this.termPosition = termPosition;
    }

}
