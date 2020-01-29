package ru.hh;

public class Document {
    public Document(long documentId, String text) {
        this.documentId = documentId;
        this.text = text;
    }
    private long documentId;
    private String text;

    public long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(long documentId) {
        this.documentId = documentId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
