import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Indexer {

    public List<Document> getDocsFromFile(String filename) {
        ArrayList<Document> docs = new ArrayList<>();
        File file = new File(filename);
        try {
            FileReader fReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fReader);
            String doc = reader.readLine();
            while (doc != null) {
                docs.add(new Document(doc));
                doc = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return docs;
    }

    public List<List<String>> getTermDocs(List<Document> docs) {
        List<List<String>> termDocs = new ArrayList<>();
        for (Document doc : docs) {
            List<String> as = List.of(doc.getPrimary().split("[\\s,;:.!?/]"));
            termDocs.add(as);
        }
        return termDocs;
    }

    public List<List<String>> getLemmatizedTermDocs(List<List<String>> termDocs) {
        return null;
    }

    public List<Map<String, Integer>> getTermPositionDocs(List<List<String>> termDocs) {
        List<Map<String, Integer>> docs = new ArrayList<>();
        for (List<String> doc : termDocs) {
            Map<String, Integer> termPosition = new HashMap<>();
            for (int i = 0; i < doc.size(); i++) {
                termPosition.put(doc.get(i), i);
            }
            docs.add(termPosition);
        }
        return docs;
    }

    public List<Map<String, Integer>> removeStopWords(List<Map<String, Integer>> termDocs) {



    }

    public ArrayList<String> getStopWordsFromFile(String filename) {
        ArrayList<String> stopWords = new ArrayList<>();
        try {
            FileReader fReader = new FileReader(filename);
            BufferedReader reader = new BufferedReader(fReader);
            String word = reader.readLine();
            while (word != null) {
                stopWords.add(word);
                word = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stopWords;
    }


}
