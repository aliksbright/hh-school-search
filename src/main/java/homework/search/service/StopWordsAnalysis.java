package homework.search.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StopWordsAnalysis {
    private final Set<String> stopWords;

    public StopWordsAnalysis() {
        Set<String> stopWords = new HashSet<>();
        try {
//            stopWords = new HashSet<>(Files.readAllLines(Paths.get("src/main/resources", "stopWords.inp"), StandardCharsets.UTF_8));
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("stopWords.inp");
            assert inputStream != null;
            InputStreamReader isReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8 );
            BufferedReader reader = new BufferedReader(isReader);
            String str;
            while((str = reader.readLine())!= null){
                stopWords.add(str);
            }
        } catch (IOException e) {
            e.getMessage();
        }
        this.stopWords = stopWords;
    }

    public List<String> execute(List<String> words) {
        return words.stream()
                .filter(word -> !stopWords.contains(word))
                .collect(Collectors.toList());
//        List<String> filterWord = new ArrayList<>();
//        for ( String word: words){
//            if (!stopWords.contains(word))
//                filterWord.add(word);
//        }
//        return filterWord;
    }
}
