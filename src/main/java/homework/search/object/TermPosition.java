package homework.search.object;

import java.util.List;
import java.util.Objects;

public class TermPosition {
    private Integer docId;
    private List<Integer> termPositions;

    public TermPosition(Integer docId, List<Integer> termPositions) {
        this.docId = docId;
        this.termPositions = termPositions;
    }

    public Integer getDocId() {
        return docId;
    }

    public List<Integer> getTermPositions() {
        return termPositions;
    }

    @Override
    public String toString() {
        return String.valueOf(docId)+ termPositions;
    }
//для сравнения объектов только по docId
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TermPosition that = (TermPosition) o;
        return Objects.equals(docId, that.docId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(docId);
    }
}
