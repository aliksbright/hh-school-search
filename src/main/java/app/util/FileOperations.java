package app.util;

import app.structure.Document;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class FileOperations {
    public static ArrayList<Document> getDocsFromFile(String filename) {
        ArrayList<Document> docs = new ArrayList<>();
        for (String doc : getLinesFromFile(filename)) {
            docs.add(new Document(doc));
        }
        return docs;
    }

    public static ArrayList<String> getLinesFromFile(String filename) {
        ArrayList<String> lines = new ArrayList<>();
        try {
            FileReader fReader = new FileReader(filename);
            BufferedReader reader = new BufferedReader(fReader);
            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }
}
