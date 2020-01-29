package hh.school.additional;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class StopWords {
    private static StopWords instance;

    private static String filePath = "stopwords.txt";
    private List<String> stopWords;

    private StopWords() {
        try {
            BufferedReader reader =  new BufferedReader(new FileReader(filePath));
            String line;
            stopWords = new ArrayList<>();
            while((line = reader.readLine()) != null) {
                stopWords.add(line.trim());
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("System exception " + e.getMessage());
        }
    }

    public static StopWords getInstance() {
        if (instance == null) {
            instance = new StopWords();
        }
        return instance;
    }

    public List<String> getStopWords() {
        return stopWords;
    }
}
