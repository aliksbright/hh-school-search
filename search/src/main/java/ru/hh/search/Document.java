package ru.hh.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Абстракция для документа.
 * @author Andrey Bukhtoyarov (andreymedoed@gmail.com).
 * @version %Id%.
 * @since 0.1.
 */
public class Document {
    /**
     * Список термов в документе.
     */
    private ArrayList<Term> terms = new ArrayList<>();
    /**
     * ИД документа.
     */
    private int lineId;

    public Document addTerm(Term term) {
        this.terms.add(term);
        return this;
    }

    public ArrayList<Term> getTerms() {
        return this.terms;
    }

    public Document setLineId(int lineId) {
        this.lineId = lineId;
        return this;
    }

    public int getLineId() {
        return this.lineId;
    }

    public List<String> getTermsString() {
        return this.terms.stream().map(Term::toString).collect(Collectors.toList());
    }

    public Set<String> getTermsStringSet() {
        return this.terms.stream().map(Term::toString).collect(Collectors.toSet());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Document document = (Document) o;
        return lineId == document.lineId &&
                Objects.equals(terms, document.terms);
    }

    @Override
    public int hashCode() {
        return Objects.hash(terms, lineId);
    }
}
