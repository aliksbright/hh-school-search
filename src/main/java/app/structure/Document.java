package app.structure;

import app.index.Indexer;
import app.util.FileOperations;

import java.util.List;

public class Document {
    private String primary;
    private List<Term> terms;

    public Document(String primary) {
        this.primary = primary;
        setTerms();
    }

    private void setTerms () {
        this.termsAndPositions = Indexer.removeStopWords(
                Indexer.getTermsAndPositions(
                        Indexer.getTerms(this)
                ), FileOperations.getLinesFromFile("stop_words.txt")
        );
    }

    public String getPrimary() {
        return primary;
    }

    public List<Term> getTerms() {
        return this.terms;
    }

}
