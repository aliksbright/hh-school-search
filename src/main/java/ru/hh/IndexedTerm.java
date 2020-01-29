package ru.hh;

import java.util.HashSet;

public class IndexedTerm {
    private String term;
    private double frequencyInCollection;
    private long countInCollection;
    public IndexedTerm(String term, long countInCollection, long documentId, int position) {
        this.term = term;
        this.countInCollection = countInCollection;
        termDocInfo = new HashSet<>();
        termDocInfo.add(new TermDocInfo(documentId, position));
    }
    HashSet<TermDocInfo> termDocInfo;
    public IndexedTerm() {}

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public double getFrequencyInCollection() {
        return frequencyInCollection;
    }

    public void setFrequencyInCollection(double frequencyInCollection) {
        this.frequencyInCollection = frequencyInCollection;
    }

    public long getCountInCollection() {
        return countInCollection;
    }

    public void setCountInCollection(long countInCollection) {
        this.countInCollection = countInCollection;
    }
}
