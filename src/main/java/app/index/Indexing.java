package app.index;

import app.structure.Document;
import app.util.FileOperations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Indexing {
    public static void runIndexing() {
        ArrayList<Document> docs = FileOperations.getDocsFromFile("./to_index.txt");

        Map<String, Integer> termDocNumber = new HashMap<>();
        Map<String, Integer> termPositionInDoc = new HashMap<>();

        for (int i = 0; i < docs.size(); i++) {
            Document doc = docs.get(i);
            Map<String, Integer> termsAndPositions = doc.getTermsAndPositions();
            for (String term : termsAndPositions.keySet()) {
                termDocNumber.put(term, i);
                termPositionInDoc.put(term, )

            }
        }


    }

}
