package index;

import java.io.*;
import java.util.*;

public class Dictionary implements Serializable {
    private static final long serialVersionUID = 1L;
    private String pathToFile;
    private Map<String, Set<String>> termDictionary;

    public Dictionary(String pathToFile) {
        this.pathToFile = pathToFile;
        termDictionary = new HashMap<>(300);
    }

    public void put(List<String> allLines) {
        for(String s : allLines)
            this.put(s);
    }

    public void put(String line) {
        String[] tokens = line.split("\\s");
        String id = tokens[0];
        for (int i = 1; i < tokens.length; i++) {
            Set<String> documentsIds = termDictionary.get(tokens[i]);
            if (documentsIds == null)
                documentsIds = new HashSet<>();
            documentsIds.add(id);
            termDictionary.put(tokens[i], documentsIds);
        }
    }

    public Map<String, Set<String>> getTermDictionary() {
        return termDictionary;
    }

    public String getPathToFile() {
        return pathToFile;
    }
}
