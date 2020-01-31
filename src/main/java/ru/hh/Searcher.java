package ru.hh;

import java.util.ArrayList;

public class Searcher {
    private Index index;
    public Searcher(IndexLoader loader) {
        index = loader.loadIndex();
    }


    public ArrayList<Document> SearchDocuments(String term) {
        var output = new ArrayList<Document>();
        var termLowerCase = term.toLowerCase().trim();
        if (index.invertedIndex.containsKey(termLowerCase)){
            var res = index.invertedIndex.get(term);
            for(var documentId: res.termDocInfoMap.keySet()){
                if(index.documents.containsKey(documentId)) {
                    output.add(index.documents.get(documentId));
                }
            }
        }
        return output;
    }

}
