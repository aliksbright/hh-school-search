package app.structure;

import java.util.List;

public class InvertedIndex {
    private String term;
    private List<Integer> docIds;
    private List<Integer> positionInDoc;

    public InvertedIndex(String term, List<Integer> docIds, List<Integer> positionInDoc) {
        this.term = term;
        this.docIds = docIds;
        this.positionInDoc = positionInDoc;
    }

    public String getTerm() {
        return term;
    }

    public List<Integer> getDocIds() {
        return docIds;
    }

    public List<Integer> getPositionInDoc() {
        return positionInDoc;
    }
}
