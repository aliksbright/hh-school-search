import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Index {
    private Map<String, List<IndexedDocument>> termDocumentMap = new HashMap<>();

    public Map<String, List<IndexedDocument>> getTermDocumentMap() {
        return termDocumentMap;
    }

    public void setTermDocumentMap(Map<String, List<IndexedDocument>> termDocumentMap) {
        this.termDocumentMap = termDocumentMap;
    }

    public void addDocument(Integer id, List<String> terms) {
        Map<String, List<Integer>> termPositions = getTermPositions(terms);

        for (String term : termPositions.keySet()) {
            if (!termDocumentMap.containsKey(term)) {
                termDocumentMap.put(term, new ArrayList<>());
            }
            IndexedDocument indexedDocument = new IndexedDocument(id);
            indexedDocument.setTermPositions(termPositions.get(term));
            termDocumentMap.get(term).add(indexedDocument);
        }
    }

    private Map<String, List<Integer>> getTermPositions(List<String> terms) {
        Map<String, List<Integer>> termPositions = new HashMap<>();

        for (int position = 0; position < terms.size(); position++) {
            String term = terms.get(position);
            if (!termPositions.containsKey(term)) {
                termPositions.put(term, new ArrayList<>());
            }
            termPositions.get(term).add(position);
        }
        return termPositions;
    }

    public void save(String indexFile) throws IOException {
        BufferedWriter mapFile = new BufferedWriter(new FileWriter(indexFile));

        for (String term : termDocumentMap.keySet()) {
            mapFile.write(term);
            mapFile.newLine();
            for (IndexedDocument document : termDocumentMap.get(term)) {
                mapFile.write(document.getDocumentId().toString());
                mapFile.write(",");
            }
            mapFile.newLine();
        }
        mapFile.close();
    }

    public void read(String indexFile) throws IOException {
        BufferedReader mapFile = new BufferedReader(new FileReader(indexFile));
        termDocumentMap.clear();

        while(true) {
            String term = mapFile.readLine();
            if (term == null) {
                break;
            }

            termDocumentMap.put(term, Arrays.stream(mapFile.readLine().split(","))
                    .map(Integer::parseInt)
                    .map(IndexedDocument::new)
                    .collect(Collectors.toList()));
        }
        mapFile.close();
    }
}
