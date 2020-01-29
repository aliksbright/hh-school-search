package hh.school.additional;

import java.io.*;
import java.util.*;

public class Terms {
/*    private static Terms instance;*/
    private String savePath;
    private FileWriter writer;
    private Map<String, Map<Integer, Integer>> index = new HashMap<>();

    public Terms(String savePath, Boolean indexing) {
        try {
            this.savePath = savePath + "/terms.txt";
            File terms = new File(this.savePath);
            if (!terms.createNewFile()) {
                BufferedReader reader = new BufferedReader(new FileReader(this.savePath));
                String line;
                while((line = reader.readLine()) != null) {
                    String[] splittedString = line.split("@");
                    Map <Integer, Integer> docIds = new HashMap<>();
                    for (int i = 1; i < splittedString.length; i++) {
                        String[] splittedDoc = splittedString[i].split(":");
                        docIds.put(Integer.parseInt(splittedDoc[0]), Integer.parseInt(splittedDoc[1]));
                    }
                    index.put(splittedString[0], docIds);
                }
                reader.close();
            }
            if (indexing) writer = new FileWriter(this.savePath, false);
        } catch (Exception e) {
            System.out.println("System exception " + e.getMessage());
        }
    }

    public void addTermsInIndex(String term, Integer docId, Integer position) {
        if (index.containsKey(term)) {
            Map<Integer, Integer> docIds = index.get(term);
            docIds.put(docId, position);
            index.put(term, docIds);
        } else {
            index.put(term, new HashMap<>(Collections.singletonMap(docId, position)));
        }
    }

    public void close() throws IOException {
        for (Map.Entry <String, Map<Integer, Integer>> entry : index.entrySet()) {
            String docsIds = "";
            for (Map.Entry<Integer, Integer> docId: entry.getValue().entrySet()) {
                docsIds += docId.getKey() + ":" + docId.getValue() + "@";
            }
            docsIds = Optional.ofNullable(docsIds)
                    .map(str -> str.replaceAll(".$", ""))
                    .orElse(docsIds);
            writer.write(entry.getKey() + "@" + docsIds + "\n");
        }
        writer.close();
    }

    public Map<String, Map<Integer, Integer>> getIndex() {
        return index;
    }
}
