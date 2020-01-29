package ru.hh;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Indexer {
    Map<String, IndexedTerm> _invertedIndex = new TreeMap<>();
    private String indexPath;
    public Indexer(String indexPath) {
        this.indexPath = indexPath;
    }

    public void addDocument(Document doc) {
        var documentId = doc.getDocumentId();
        var words = Arrays.stream(doc.getText().trim().split(" ")).map(String::toLowerCase).collect(Collectors.toList());
        var count = words.size();
        int position = 0;
        for(var s :words) {
            if (!_invertedIndex.containsKey(s)){
                _invertedIndex.put(s, new IndexedTerm(s, 1, documentId, position));
            } else {
                var term = _invertedIndex.get(s);
                // todo: make well
                term.termDocInfo.add(new TermDocInfo());
            }
            position++;
        }

    }

    public void SaveIndex() {

    }
}
