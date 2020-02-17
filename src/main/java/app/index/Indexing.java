package app.index;

import app.structure.Document;
import app.structure.TermInv;
import app.structure.Term;
import app.util.FileOperations;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Indexing {
    public static void runIndexing() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Write original file name to index:");
        String originalFile = scanner.next();

        while (!new File(originalFile).canRead()) {
            System.out.println("File does not exist! Write correct file name to index:");
            originalFile = scanner.next();
        }

        System.out.println("Write index file name");
        String indexFile = scanner.next();

        System.out.println("Indexing running...");

        ArrayList<Document> docs = FileOperations.getDocsFromFile(originalFile);
        HashMap<String, TermInv> invIndex = getInvertedIndex(docs);
        FileOperations.invIndexToJson(invIndex, indexFile);

        System.out.println("Success!");
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
