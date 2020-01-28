package homework.search.object;

import java.util.List;

public class Document {
    private Integer docId;
    private List<String> terms;

    public Document(Integer docId, List<String> terms) {
        this.docId = docId;
        this.terms = terms;
    }

    public Integer getDocId() {
        return docId;
    }

    public void setDocId(Integer docId) {
        this.docId = docId;
    }

    public List<String> getTerms() {
        return terms;
    }

    public void setTerms(List<String> terms) {
        this.terms = terms;
    }
}
