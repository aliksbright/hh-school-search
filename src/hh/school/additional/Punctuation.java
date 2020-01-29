package hh.school.additional;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Punctuation {
    private static Punctuation instance;

    private static String filePath = "punctuation.txt";
    private List<String> punctuation;

    private Punctuation() {
        try {
            BufferedReader reader =  new BufferedReader(new FileReader(filePath));
            String line;
            punctuation = new ArrayList<>();
            while((line = reader.readLine()) != null) {
                punctuation.add(line);
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("System exception " + e.getMessage());
        }
    }

    public static Punctuation getInstance() {
        if (instance == null) {
            instance = new Punctuation();
        }
        return instance;
    }

    public List<String> getPunctuation() {
        return punctuation;
    }
}
