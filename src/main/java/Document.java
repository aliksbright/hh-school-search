import java.util.HashMap;
import java.util.HashSet;

public class Document {
    private String primary;
    private HashMap<String, Integer> termPosition;
    private HashSet<String> index;

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

    public HashSet<String> getIndex() {
        return index;
    }

    public void setIndex(HashSet<String> index) {
        this.index = index;
    }
}
