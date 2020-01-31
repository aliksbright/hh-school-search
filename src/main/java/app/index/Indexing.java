package app.index;

import app.structure.Document;
import app.structure.InvertedIndex;
import app.structure.Term;
import app.util.FileOperations;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Indexing {
    public static void runIndexing() {
        ArrayList<Document> docs = FileOperations.getDocsFromFile("./to_index.txt");

        HashMap<String, InvertedIndex> invIndex = getInvertedIndex(docs);

        Gson gson = new Gson();
        String jsonInvIndex = gson.toJson(invIndex);
        try {
            FileWriter fw = new FileWriter(new File("inv_index.json"));
            fw.write(jsonInvIndex);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static HashMap<String, InvertedIndex> getInvertedIndex(List<Document> docs) {
        HashMap<String, InvertedIndex> invIndex = new HashMap<>();
        for (int i = 0; i < docs.size(); i++) {
            Document doc = docs.get(i);
            for (Term term : doc.getTerms()) {
                InvertedIndex index = invIndex.getOrDefault(term.getValue(), null);
                if (index == null) {
                    invIndex.put(term.getValue(), new InvertedIndex(i, term.getPosition()));
                } else {
                    index.addDoc(i, term.getPosition());
                }
            }
        }
        return invIndex;
    }

}
