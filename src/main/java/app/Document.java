package app;

import app.index.Indexer;
import app.util.FileOperations;

import java.util.Map;

public class Document {
    private String primary;
    private Map<String, Integer> termsAndPositions;

    public Document(String primary) {
        this.primary = primary;
        setTermPosition();
    }

    private void setTermPosition () {
        Indexer indexer = new Indexer();
        this.termsAndPositions = indexer.removeStopWords(
                indexer.getTermsAndPositions(
                        indexer.getTerms(this)
                ), FileOperations.getLinesFromFile("stop_words.txt")
        );
    }

    public String getPrimary() {
        return primary;
    }

    public Map<String, Integer> getTermsAndPositions() {
        return termsAndPositions;
    }

}
