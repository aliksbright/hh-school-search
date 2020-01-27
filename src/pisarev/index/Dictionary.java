package pisarev.index;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Dictionary implements Serializable {
    private Map<String, Set<Integer>> termDictionary;

    public Dictionary(String pathToFile) throws IOException, ClassNotFoundException {
        if (Files.exists(Path.of(pathToFile))) {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(pathToFile));
            termDictionary = ((Dictionary) ois.readObject()).getTermDictionary();
            ois.close();
        } else {
            termDictionary = new HashMap<>();
        }
    }

    public void put(List<String> allLines) {
        for(String s : allLines)
            this.put(s);
    }

    public void put(String line) {
        String[] arrTerms = line.split(" ");
        Set<Integer> documentsIds = new HashSet<>();
        documentsIds.add(Integer.parseInt(arrTerms[0]));
        for (int i = 1; i < arrTerms.length; i++) {
            if (termDictionary.containsKey(arrTerms[i]))
                documentsIds.addAll(termDictionary.get(arrTerms[i]));
            termDictionary.put(arrTerms[i], documentsIds);
        }
    }

    public void serialize(String pathToFile) throws IOException {
        if (Files.exists(Path.of(pathToFile))) {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(pathToFile));
            oos.writeObject(this);
            oos.close();
        }
    }

    public Map<String, Set<Integer>> getTermDictionary() {
        return termDictionary;
    }

    public void setTermDictionary(Map<String, Set<Integer>> termDictionary) {
        this.termDictionary = termDictionary;
    }
}
