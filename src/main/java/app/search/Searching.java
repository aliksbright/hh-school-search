package app.search;

import app.structure.TermInv;
import app.util.FileOperations;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

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

        List<Integer> requestedDocs = getRequestedDocs(invIndex, query);

        printRequestedDocs(requestedDocs);
    }

    public static List<Integer> getRequestedDocs(HashMap<String, TermInv> invIndex, String query) {
        return null;
    }

    public static void printRequestedDocs(List<Integer> requestedDocs) {
        List<String> stringDocs = new ArrayList<>();
        for (int id : requestedDocs) {
            stringDocs.add(String.valueOf(id));
        }
        System.out.println(String.join(", ", stringDocs));
    }
}
