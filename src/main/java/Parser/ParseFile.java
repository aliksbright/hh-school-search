package Parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class ParseFile {

    private ArrayList<String> delWords = new ArrayList<>(Arrays.asList("in", "out", "as", "at"));

    private HashSet<String> document = new HashSet<>();

    public ParseFile(Path filePath) throws Exception {
        this.parseFile(filePath);
    }

    private void parseFile(Path filePath) throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))){
            try {
                while (reader.ready()) {
                    String[] line = reader.readLine().split(" ");
                    for (int i = 0; i < line.length; i++) {
                        line[i] = line[i].replaceAll("[,.{}()!?/\"'%;@#*|><]", "");
                        if (!delWords.contains(line[i]))
                            document.add(line[i]);
                    }
                }
            }
            catch (ArrayIndexOutOfBoundsException | IOException e){
                System.out.println("Файл  неверен...");
            }
        }
    }

    public HashSet<String> getDocument() {
        return document;
    }
}
