package app.search;

import app.structure.TermInv;
import app.util.FileOperations;

import java.io.File;
import java.util.*;

public class Searching {
    public static void runSearching() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Write index file name:");
        String indexFile = scanner.next();
        while (!new File(indexFile).canRead()) {
            System.out.println("File does not exist! Write correct index file name:");
            indexFile = scanner.next();
        }

        System.out.println("Write your search query:");
        String query = scanner.next();

        HashMap<String, TermInv> invIndex = FileOperations.jsonToInvIndex(indexFile);

        Set<Integer> requestedDocs = getRequestedDocs(invIndex, query);

        printRequestedDocs(requestedDocs);
    }

    public static Set<Integer> getRequestedDocs(HashMap<String, TermInv> invIndex, String query) {
        Searcher searcher = new Searcher(query, invIndex);
        return searcher.getRequestedDocs();
    }

    public static void printRequestedDocs(Set<Integer> requestedDocs) {
        if (requestedDocs.contains(-1)) {
            System.out.println("Nothing found");
        } else {
            List<String> stringDocs = new ArrayList<>();
            for (int id : requestedDocs) {
                stringDocs.add(String.valueOf(id));
            }
            System.out.println("Requested document ids:");
            System.out.println(String.join(", ", stringDocs));
        }
    }
}
