package app.util;

import app.structure.Document;
import app.structure.TermInv;
import app.structure.InvertedIndex;
import com.google.gson.Gson;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

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
        try (FileReader fReader = new FileReader(filename);
             BufferedReader reader = new BufferedReader(fReader)) {
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

    public static void invIndexToJson(HashMap<String, TermInv> invIndexMap, String filename) {
        InvertedIndex invertedIndex = new InvertedIndex();
        invertedIndex.invIndex = invIndexMap;   // HashMap не мог нормально парситься Gson'ом

        Gson gson = new Gson();
        File file = new File(filename);
        String jsonInvIndex = gson.toJson(invertedIndex);
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(jsonInvIndex);
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static HashMap<String, TermInv> jsonToInvIndex(String filename) {
        Gson gson = new Gson();
        File file = new File(filename);

        String jsonIndex = null;
        try (FileReader fr = new FileReader(file);
             BufferedReader bReader = new BufferedReader(fr)) {
            jsonIndex = bReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return gson.fromJson(jsonIndex, InvertedIndex.class).invIndex;
    }
}
