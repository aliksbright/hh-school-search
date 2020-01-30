package hhYandexTest.InverseIndexer;

import java.util.ArrayList;
import java.util.List;

public  class DocInfo {
    public long docId;
    public List<Integer> termPosition;
    public long docBlockSize;
    public long totalDocuments;


    public DocInfo(long docId, long docBlockSize, long totalDocs) {
        this.docId = docId;
        this.docBlockSize = docBlockSize;
        this.totalDocuments = totalDocs;
    }

    public DocInfo(long docId, int termPosition, long docBlockSize, long totalDocs) {
        this(docId, docBlockSize, totalDocs);
        this.termPosition = List.of(termPosition);
    }
    public DocInfo(long docId, List<Integer> termPositions, long docBlockSize, long totalDocs) {
        this(docId, docBlockSize, totalDocs);
        this.termPosition = termPositions;
    }
}
