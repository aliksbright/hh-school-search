import java.util.HashMap;

public class Document {
    private String primary;
    private HashMap<String, Integer> termsAndPositions;

    public Document(String primary) {
        this.primary = primary;
    }

    private void setTermPosition () {

    }

    public String getPrimary() {
        return primary;
    }

    public HashMap<String, Integer> getTermsAndPositions() {
        return termsAndPositions;
    }

}
