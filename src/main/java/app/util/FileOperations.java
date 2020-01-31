package app.util;

import app.structure.Document;
import app.structure.TermInv;
import app.structure.InvertedIndex;
import com.google.gson.Gson;

import java.io.*;
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

    public static void invIndexToJson(InvertedIndex invIndex, String filename) {
        Gson gson = new Gson();
        File file = new File(filename);

        InvertedIndex old = null;
        FileReader fr;
        BufferedReader bReader;

        String jsonInvIndex = "";

        if (file.canRead()) {
            try {
                fr = new FileReader(file);
                bReader = new BufferedReader(fr);
                old = gson.fromJson(bReader.readLine(), InvertedIndex.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (String term : invIndex.invIndex.keySet()) {
                TermInv termInv = old.invIndex.getOrDefault(term, null);
                if (termInv == null) {
                    old.invIndex.put(term, invIndex.invIndex.get(term));
                } else {
                    old.invIndex.get(term).addAllDocs(invIndex.invIndex.get(term).getDocIds(), invIndex.invIndex.get(term).getPositionInDoc());
                }
            }
            jsonInvIndex = gson.toJson(old);

        } else {
            jsonInvIndex = gson.toJson(invIndex);
        }

        try {
            FileWriter fw = new FileWriter(file);
            fw.write(jsonInvIndex);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
