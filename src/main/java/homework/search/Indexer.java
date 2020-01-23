package homework.search;

import homework.search.service.Indexing;
import homework.search.service.readFile;

import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;

public class Indexer {
    private final Indexing indexing;
//    String filePath;

    public Indexer(String filePath) {
        indexing = new Indexing(filePath);
//        this.filePath = filePath;
    }


    public void writeIndex(String inputFile) {
        List<String> inpDoc = readFile.readInputFile(inputFile);//каждая строка - новый документ для поиска
        HashMap<String, SortedSet<Integer>> invertIndex = Indexing.getInvertIndex(inpDoc);
        Indexing.writeIndexFile(invertIndex);
    }
}
