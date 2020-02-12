package hhYandexTest.Searcher;

import hhYandexTest.InverseIndexer.DocInfo;
import hhYandexTest.InverseIndexer.TermManager;

import java.util.ArrayList;
import java.util.List;

public class SearchOrEntry extends SearchEntry {

    public SearchOrEntry() {
        super();
    }


    public DocInfo peek() {
        // Get current document, not removing it from stack
        return current;
    }

    SearchEntry     masterEntry, slaveEntry;
    DocInfo         masterDoc, slaveDoc;

    void firstPull() {
        DocInfo         lInfo;
        DocInfo         rInfo;

        lInfo = left.pull();
        rInfo = right.pull();

        if (lInfo == null && rInfo == null) {
            return;
        }

        masterDoc   = lInfo;
        masterEntry = left;
        slaveDoc    = rInfo;
        slaveEntry  = right;

        if (masterDoc == null)
            swapMasterSlave();

    }

    void swapMasterSlave() {
        DocInfo     doc     = masterDoc;
        SearchEntry entry   = masterEntry;
        masterDoc   = slaveDoc;
        masterEntry = slaveEntry;
        slaveDoc    = doc;
        slaveEntry  = entry;
    }

    public DocInfo pull() {
        if (terminated) {
            current = null;
            return null;
        }

        if (current == null) {
            firstPull();
        }

        if (slaveDoc == null) {
            current     = masterDoc;
            masterDoc   = masterEntry.pull();
            if (masterDoc == null)
                terminated = true;
            return current;
        } else if (masterDoc.docId > slaveDoc.docId) {
            current     = slaveDoc;
            slaveDoc    = slaveEntry.pull();
            return current;
        } else if (masterDoc.docId == slaveDoc.docId) {
            long maxSize = slaveDoc.totalDocuments + masterDoc.totalDocuments;
            List<Integer> termPositions = new ArrayList<>(masterDoc.termPosition);

            termPositions.addAll(slaveDoc.termPosition);
            current     =  new DocInfo(masterDoc.docId, termPositions, 0, maxSize);
            slaveDoc    = slaveEntry.pull();
            masterDoc   = masterEntry.pull();

            if (slaveDoc == null && masterDoc == null)
                terminated = true;
            else if (masterDoc == null)
                swapMasterSlave();

            return  current;
        }

        swapMasterSlave();

        current     = slaveDoc;
        slaveDoc    = slaveEntry.pull();

        return current;
    }
    @Override
    public void setTermManager(TermManager manager) throws Exception{
        throw new Exception("Term operand not supported by " + this.getClass().getName());
    }

    @Override
    public void test() {
        System.out.print(" OR( ");
        left.test();
        System.out.print(" , ");
        right.test();
        System.out.print(" ) ");
    }
}
