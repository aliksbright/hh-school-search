package hhYandexTest.Searcher;

import hhYandexTest.InverseIndexer.DocInfo;
import hhYandexTest.InverseIndexer.TermManager;

import java.util.ArrayList;
import java.util.List;

public class SearchAndEntry extends SearchEntry {
    boolean near = false;

    public SearchAndEntry() {
        super();
        near = false;
    }
    public SearchAndEntry(boolean near) {
        this();
        this.near = near;
    }

    @Override
    public void setTermManager(TermManager manager) throws Exception{
        throw new Exception("Term operand not supported by " + this.getClass().getName());
    }

    @Override
    public void test() {
       System.out.print(" AND( ");
       left.test();
        System.out.print(" , ");
        right.test();
        System.out.print(" ) ");
    }

    public DocInfo peek() {
        // Get current document, not removing it from stack
        return current;
    }
    SearchEntry     masterEntry, slaveEntry;
    DocInfo         masterDoc, slaveDoc;
    boolean         masterIsLeft = false;

    void firstPull() {
        DocInfo         lInfo;
        DocInfo         rInfo;

        lInfo = left.pull();
        rInfo = right.pull();

        if (lInfo == null || rInfo == null)
            return;

        if (lInfo.totalDocuments < rInfo.totalDocuments) {
            masterDoc   = lInfo;
            masterEntry = left;
            slaveDoc    = rInfo;
            slaveEntry  = right;
            masterIsLeft = true;
        } else {
            masterDoc   = rInfo;
            masterEntry = right;
            slaveDoc    = lInfo;
            slaveEntry  = left;
            masterIsLeft = false;
        }

        //System.out.println("   Master term : " + masterEntry.term);
        //System.out.println("   Slave  term : " + slaveEntry.term);

    }

    public DocInfo pull() {
        if (terminated)
            return null;

        if (current == null) {
            //System.out.println("And INIT : ");
            firstPull();
        } else {
            slaveDoc = slaveEntry.pull();
            //System.out.println("And Pull : "  + (slaveDoc == null ? "null" : slaveDoc.docId));
        }

        pull_loop:
        while (masterDoc != null && slaveDoc != null) {
            while (masterDoc.docId > slaveDoc.docId) {
                slaveDoc = slaveEntry.pull();
                if (slaveDoc == null)
                    break pull_loop;
            }

           if (masterDoc.docId == slaveDoc.docId) {
               boolean  emit = true;

               if (near) {
                   Integer leftPos, rightPos;
                   if (masterIsLeft) {
                       // TODO : Here may be issues with compound statements as left and right term positions
                       leftPos = masterDoc.termPosition.get(0);
                       rightPos = slaveDoc.termPosition.get(0);
                   } else {
                       leftPos = slaveDoc.termPosition.get(0);
                       rightPos = masterDoc.termPosition.get(0);
                   }

                   if (leftPos != rightPos -1)
                       emit = false;
               }

               if (emit) {
                   long maxSize = Math.max(slaveDoc.totalDocuments, masterDoc.totalDocuments);
                   List<Integer> termPositions = new ArrayList<>(masterDoc.termPosition);

                   //System.out.println("Emitting " + masterDoc.docId);
                   termPositions.addAll(slaveDoc.termPosition);
                   current = new DocInfo(masterDoc.docId, termPositions, 0, maxSize);
                   return current;
               }
           }

           masterDoc = masterEntry.pull();
        }

        terminated = true;
        current = null;
        return null;
    }

}
