package app.index;

import app.Document;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Indexer {

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

}
