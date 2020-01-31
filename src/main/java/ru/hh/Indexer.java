package ru.hh;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Indexer implements Serializable {

    Index index;
    private IndexLoader loader;
    public Indexer(IndexLoader  loader) {
        this.loader = loader;
        this.index = new Index();
    }

    public void addDocuments(ArrayList<Document> documents){
        for(var doc: documents) {
            addDocument(doc);
            index.documents.put(doc.getDocumentId(), doc);
        }

    }

    private void addDocument(Document doc) {
        var documentId = doc.getDocumentId();
        var words = Arrays.stream(doc.getText().trim().split(" "))
                .map(s -> StringUtils.strip(s, "“"))
                .map(s -> StringUtils.strip(s, "”"))
                .map(s -> StringUtils.strip(s, "."))
                .map(s -> StringUtils.strip(s, ","))
                .map(s -> StringUtils.strip(s, "?"))
                .map(s -> StringUtils.strip(s, ";"))
                .map(String::toLowerCase).collect(Collectors.toList());
        int position = 0;
        for(var s :words) {
            TermDocuments termDocuments;
            if (!index.invertedIndex.containsKey(s)){
                termDocuments = new TermDocuments();

            } else {
                termDocuments = index.invertedIndex.get(s);
            }
            termDocuments.addDocument(documentId, position);
            index.invertedIndex.put(s, termDocuments);
            position++;
        }
        index.wordsCount += words.size();;
    }

    public TermDocuments getTerm(String term){
        return index.invertedIndex.get(term);
    }

    public void SaveIndex() {
        loader.saveIndex(index);
    }

    public void LoadIndex(){
        index = loader.loadIndex();
    }
}
