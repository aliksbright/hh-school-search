package ru.hh;

public class DocumentIndexer {
    private final Indexer indexer;
    private final DocumentReader documentReader;

    public DocumentIndexer(Indexer indexer, DocumentReader documentReader) {

        this.indexer = indexer;
        this.documentReader = documentReader;
    }

    public void Index(){
        var documents = documentReader.GetDocuments();
        indexer.addDocuments(documents);

    }
}
