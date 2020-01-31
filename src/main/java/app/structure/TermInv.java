package app.structure;

import java.util.ArrayList;
import java.util.List;

public class Index {
    private List<Integer> docIds;
    private List<Integer> positionsInDoc;

    public Index(int docId, int positionInDoc) {
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

    public void addAllDocs(List<Integer> docIds, List<Integer> positionsInDoc) {
        this.docIds.addAll(docIds);
        this.positionsInDoc.addAll(positionsInDoc);
    }
}
