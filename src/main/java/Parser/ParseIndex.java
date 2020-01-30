package Parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class ParseIndex {

    private LinkedHashMap<String, ArrayList<String>> document = new LinkedHashMap<>();

    public ParseIndex(Path indexPath) throws Exception {
        this.parseIndexFile(indexPath);
    }

    private void parseIndexFile(Path indexPath) throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(indexPath.toFile()))){
            try {
                while (reader.ready()) {
                    String[] line = reader.readLine().split(">");
                    ArrayList<String> list = new ArrayList<>();
                    for (int i = 1; i < line.length; i++) {
                        list.add(line[i].toLowerCase());
                    }
                    document.put(line[0], list);
                }
            }
            catch (ArrayIndexOutOfBoundsException | IOException e){
                System.out.println("Файл индекса неверен...");
            }
        }
    }

    public LinkedHashMap<String, ArrayList<String>> getDocument() {
        return document;
    }
}
