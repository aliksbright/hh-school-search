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

    public List<String> getTerms(Document doc) {
        return List.of(doc.getPrimary().split("[\\s,;:.!?/]"));
    }

    public List<String> getLemmatizedTerms(List<String> terms) {
        return null;
    }

    public Map<String, Integer> getTermsAndPositions(List<String> terms) {
            Map<String, Integer> termPosition = new HashMap<>();
            for (int i = 0; i < terms.size(); i++) {
                termPosition.put(terms.get(i), i);
            }
        return termPosition;
    }

    public Map<String, Integer> removeStopWords(Map<String, Integer> terms, ArrayList<String> stopWords) {
        for (String word : stopWords) {
                terms.remove(word);
        }
        return terms;
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
