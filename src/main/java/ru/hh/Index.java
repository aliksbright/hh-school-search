package ru.hh;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Index implements Serializable {
    public long wordsCount;
    public Map<String, TermDocuments> invertedIndex;
    public Map<Long, Document> documents;
    public Index() {
        invertedIndex = new HashMap<>();
        documents= new HashMap<>();
    }
}
