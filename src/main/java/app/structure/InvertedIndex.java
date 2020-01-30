package app.structure;

import java.util.ArrayList;
import java.util.List;

public class InvertedIndex {
    private List<Integer> docIds;
    private List<Integer> positionsInDoc;

    public InvertedIndex(int docId, int positionInDoc) {
        this.docIds = new ArrayList<>();
        this.docIds.add(docId);
        this.positionsInDoc = new ArrayList<>();
        this.positionsInDoc.add(positionInDoc);
    }

    public List<Integer> getDocIds() {
        return docIds;
    }

    public List<Integer> getPositionInDoc() {
        return positionsInDoc;
    }

    public void addDoc(int docId, int positionInDoc) {
        this.docIds.add(docId);
        this.positionsInDoc.add(positionInDoc);
    }
}
