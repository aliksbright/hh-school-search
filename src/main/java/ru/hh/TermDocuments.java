package ru.hh;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TermDocuments implements Serializable {
    private long countInCollection;
    Map<Long, DocumentInfo> termDocInfoMap;
    public TermDocuments() {
        termDocInfoMap = new HashMap<>();
    }

    public void addDocument(long documentId, int position){
        DocumentInfo documentInfo;
        if (termDocInfoMap.containsKey(documentId)){
            documentInfo = termDocInfoMap.get(documentId);
        } else {
            documentInfo = new DocumentInfo();
        }
        documentInfo.addPosition(position);
        termDocInfoMap.put(documentId, documentInfo);
        countInCollection++;
    }

    public Set<Long> getDocumentIds(){
        return termDocInfoMap.keySet();
    }

    public ArrayList<Integer> getDocumentPositions(long documentId){
        if (termDocInfoMap.containsKey(documentId)){
           var termDocInfo = termDocInfoMap.get(documentId);
           return termDocInfo.GetPositions();
        }
        return null;
    }

    public long getTermCount(){
        return countInCollection;
    }
}
