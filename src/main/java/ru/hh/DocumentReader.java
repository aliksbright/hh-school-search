package ru.hh;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class DocumentReader {

    private String documentPath;
    public DocumentReader(String documentPath){
        this.documentPath = documentPath;
    }

    public ArrayList<Document> GetDocuments() {
        var documents = new ArrayList<Document>();
        long documentId = 1;
        try (Scanner scanner = new Scanner(new File(documentPath));){
            while (scanner.hasNextLine()) {
                var line = scanner.nextLine();
                documents.add(new Document(documentId++, line));
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return documents;
    }
}
