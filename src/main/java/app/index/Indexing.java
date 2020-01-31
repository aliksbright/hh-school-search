package app.index;

import app.structure.Document;
import app.structure.TermInv;
import app.structure.InvertedIndex;
import app.structure.Term;
import app.util.FileOperations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Indexing {
    public static void runIndexing() {
        ArrayList<Document> docs = FileOperations.getDocsFromFile("./to_index.txt");

        HashMap<String, TermInv> invIndex = getInvertedIndex(docs);

        FileOperations.invIndexToJson(invIndex, "inv_index.json");

    }

    public static HashMap<String, TermInv> getInvertedIndex(List<Document> docs) {
        HashMap<String, TermInv> invIndex = new HashMap<>();
        for (int i = 0; i < docs.size(); i++) {
            Document doc = docs.get(i);
            for (Term term : doc.getTerms()) {
                TermInv termInv = invIndex.getOrDefault(term.getValue(), null);
                if (termInv == null) {
                    invIndex.put(term.getValue(), new TermInv(i, term.getPosition()));
                } else {
                    termInv.addDoc(i, term.getPosition());
                }
            }
        }
        return invIndex;
    }

}
