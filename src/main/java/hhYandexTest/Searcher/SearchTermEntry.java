package hhYandexTest.Searcher;

import hhYandexTest.InverseIndexer.DocInfo;
import hhYandexTest.InverseIndexer.InverseIndex;

public class SearchTermEntry extends SearchEntry {
    int    termPosition;

    public void setInverseIndex(InverseIndex index) {
        inverseIndex = index;
        manager = index.getTermManager(this.term);
        termPosition = 0;
    }

    public void rewind() {
        termPosition = 0;
        terminated = false;
        current = null;
    }

    public DocInfo peek() {
        // Get current document, not removing it from stack
        return current;
    }

    public DocInfo pull() {
        if (terminated || manager == null)
            return null;

        if (current != null) {
            termPosition += current.docBlockSize;
            //System.out.println(this.term + " : Advanced pointer " + current.docBlockSize);
        }

        current = manager.getDocInfo(termPosition);
        //System.out.println(this.term + " : returning " + current.docId);
        return current;
    }

    public SearchTermEntry(String term) {
        this.term = term;
    }

    @Override
    public void setLeft(SearchEntry left) throws Exception {
        throw new Exception("Left operand not supported by "+ this.getClass().getName());
    }

    @Override
    public void setRight(SearchEntry left) throws Exception {
        throw new Exception("Right operand not supported by " + this.getClass().getName());
    }

    @Override
    public void test() {
        System.out.print(" '" + term + "' ");
    }
}
