package app.index;

import app.structure.Document;
import app.util.FileOperations;

import java.util.ArrayList;

public class Indexing {
    public static void runIndexing() {
        ArrayList<Document> docs = FileOperations.getDocsFromFile("./to_index.txt");


    }

}
