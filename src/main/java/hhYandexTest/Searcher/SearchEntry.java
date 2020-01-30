package hhYandexTest.Searcher;

import hhYandexTest.InverseIndexer.DocInfo;
import hhYandexTest.InverseIndexer.InverseIndex;
import hhYandexTest.InverseIndexer.TermManager;

import java.util.ArrayList;
import java.util.List;

public class SearchEntry {
    SearchEntry     left;
    SearchEntry     right;
    TermManager     manager;
    String          term;
    InverseIndex    inverseIndex;
    DocInfo         current;
    boolean         terminated;
    long            totalDocsuments;


    public SearchEntry() {
        left = null;
        right = null;
        current = null;
        terminated = false;
    }

    public void setLeft(SearchEntry left) throws Exception {
       this.left = left;
    }

    public void setRight(SearchEntry right) throws Exception {
        this.right = right;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public void setTermManager(TermManager manager) throws Exception{
        this.manager = manager;
    }

    public DocInfo peek() {
        // Get current document, not removing it from stack
        return null;
    }

    public DocInfo pull() {
        // Get next document and remove it
        return null;
    }

    public void rewind() {
        if (left != null)
            left.rewind();
        if (right != null)
            right.rewind();
    }

    public void setInverseIndex(InverseIndex index) {
        if (left != null)
            left.setInverseIndex(index);
        if (right != null)
            right.setInverseIndex(index);
        this.inverseIndex = index;
    }

    void setTotalDocsuments(long totalDocsuments) {
        this.totalDocsuments = totalDocsuments;
    }

    public void test() {
        System.out.println("Test function");
    }
}


