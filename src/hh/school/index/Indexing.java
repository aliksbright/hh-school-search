package hh.school.index;

import hh.school.additional.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Indexing {

    public Indexing(String savePath, String filePath) {
        try {
            BufferedReader reader =  new BufferedReader(new FileReader(filePath));
            String line;
            Documents docs = new Documents(savePath, true);
            Terms terms = new Terms(savePath, true);
            while ((line = reader.readLine()) != null) {
                List<String> tokens = tokenizing(line);
                tokens = filtering(tokens);
                tokens = stemming(tokens);
                Integer docId = docs.addLineInDocuments(line);
                Integer position = 0;
                for (String token: tokens) {
                    terms.addTermsInIndex(token, docId, position);
                    position++;
                }
            }
            reader.close();
            docs.close();
            terms.close();
        } catch (Exception e) {
            System.out.println("System exception " + e.getMessage());
        }
    }

    private List<String> tokenizing(String string) {
        List<String> words = Arrays.asList(string.split("\\b"));
        Punctuation punctuation = Punctuation.getInstance();
        return words.stream()
                .map(word -> word.trim())
                .filter(word -> !punctuation.getPunctuation().contains(word))
                .collect(Collectors.toList());
    }

    private List<String> filtering(List<String> tokens) {
        StopWords stopWords = StopWords.getInstance();
        return tokens.stream()
                .filter(word -> !stopWords.getStopWords().contains(word))
                .collect(Collectors.toList());
    }

    private List<String> stemming(List<String> tokens) {
        PorterStem stem = new PorterStem();
        return tokens.stream()
                .map(token -> stem.stem(token))
                .collect(Collectors.toList());
    }


}
