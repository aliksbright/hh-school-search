package hhYandexTest.Searcher;

import hhYandexTest.InverseIndexer.DocInfo;

public class SearchNotEntry extends SearchEntry {
    long    docId;
    DocInfo notDoc;

    public DocInfo pull() {
        if (terminated) {
            current = null;
            return null;
        }

        if (current == null) {
            notDoc = right.pull();
            docId = 0;
        }

        if (notDoc == null || docId < notDoc.docId) {
            current     =  new DocInfo(docId, 0, 0, totalDocuments);
            docId++;
            if (docId >= totalDocuments)
                terminated = true;
            return current;
        }

        // Skip similar documents
        while (docId == notDoc.docId) {
            while (notDoc != null && docId == notDoc.docId)
                notDoc = right.pull();
            docId++;

            if (docId >= totalDocuments) {
                terminated = true;
                return null;
            }

            if (notDoc == null)
                break;
        }

        current = new DocInfo(docId, 0, 0, totalDocuments);
        docId++;

        if (docId >= totalDocuments)
            terminated = true;

        return current;
    }

    @Override
    public void test() {
        System.out.print(" NOT( ");
        right.test();
        System.out.print(" ) ");
    }

}
