package ru.hh;

public class TermDocInfo {
    public TermDocInfo() { }

    public TermDocInfo(long docId, int position) {
        this.docId = docId;
        this.position = position;
    }
    private long docId;
    private int position;
    private double documentFrequency;

    public long getDocId() {
        return docId;
    }

    public void setDocId(long docId) {
        this.docId = docId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public double getDocumentFrequency() {
        return documentFrequency;
    }

    public void setDocumentFrequency(double documentFrequency) {
        this.documentFrequency = documentFrequency;
    }
}
