import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Indexer {

    public ArrayList<Document> getDocsFromFile(String filename) {
        ArrayList<Document> docs = new ArrayList<>();
        for (String doc : getLinesFromFile(filename)) {
            docs.add(new Document(doc));
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

    public List<Map<String, Integer>> removeStopWords(List<Map<String, Integer>> termDocs, ArrayList<String> stopWords) {
        for (String word : stopWords) {
            for (Map<String, Integer> doc : termDocs) {
                doc.remove(word);
            }
        }
        return termDocs;
    }

    public ArrayList<String> getLinesFromFile(String filename) {
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
