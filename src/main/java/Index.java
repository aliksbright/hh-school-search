import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Index {

    private List<String> file;

    public Index(String pathToFile) throws IOException {
        file = Files.readAllLines(Paths.get(pathToFile));
    }

    public HashMap<String, Map<Integer, List<Integer>>> makeIndex() {
        /* Read file with documents and create Map of Maps:
         - External keys: terms
         -- Internal keys: document id
         --- Values: positions in doc
         */
        HashMap<String, Map<Integer, List<Integer>>> dict = new HashMap<>();
        int docNumber = 0;
        for (String doc : file) {
            String[] terms = doc.split(" ");
            int position = 0;
            for (String term : terms) {
                if (!dict.containsKey(term.toLowerCase())) {
                    Map<Integer, List<Integer>> map = new HashMap<>();
                    map.put(docNumber, Collections.singletonList(position));
                    dict.put(term.toLowerCase(), map);
                } else {
                    if (dict.get(term.toLowerCase()).containsKey(docNumber)) {
                        dict.get(term.toLowerCase()).get(docNumber).add(position);
                    } else {
                        dict.get(term.toLowerCase()).put(docNumber, Collections.singletonList(position));
                    }
                }
                position++;
            }
            docNumber++;
        }
        return dict;
    }

    public void saveIndex(String savePath, HashMap<String, Map<Integer, List<Integer>>> index) {
        try {
            String jsonString = new ObjectMapper().writeValueAsString(index);
            System.out.println(jsonString);
            Files.write(Paths.get(savePath), jsonString.getBytes());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, Map<Integer, List<Integer>>> readIndex() throws IOException {
        if (file.isEmpty()) {
            System.out.println("Index is empty");
            System.exit(0);
        }
        HashMap<String, Map<String, List<Integer>>> readIndex = new HashMap<>();
        HashMap<String, Map<Integer, List<Integer>>> index = new HashMap<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            readIndex = mapper.readValue(file.get(0), HashMap.class);
            for (String term: readIndex.keySet()) {
                Map<Integer, List<Integer>> map = new HashMap<>();
                for (String doc: readIndex.get(term).keySet()) {
                    map.put(Integer.parseInt(doc), readIndex.get(term).get(doc));
                    index.put(term.toLowerCase(), map);
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return index;
    }
}
