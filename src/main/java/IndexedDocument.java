import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class IndexedDocument implements Serializable {
    private Integer documentId;
    private List<Integer> termPositions;

    public IndexedDocument(Integer documentId) {
        this.documentId = documentId;
    }

    public Integer getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Integer documentId) {
        this.documentId = documentId;
    }

    public List<Integer> getTermPositions() {
        return termPositions;
    }

    public void setTermPositions(List<Integer> termPositions) {
        this.termPositions = termPositions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IndexedDocument that = (IndexedDocument) o;
        return Objects.equals(documentId, that.documentId) &&
                Objects.equals(termPositions, that.termPositions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(documentId, termPositions);
    }
}
