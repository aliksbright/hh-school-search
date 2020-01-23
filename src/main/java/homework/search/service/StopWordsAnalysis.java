package homework.search.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StopWordsAnalysis {
    private final Set<String> stopWords;

    public StopWordsAnalysis() {
        Set<String> stopWords = null;
        try {
            stopWords = new HashSet<>(Files.readAllLines(Paths.get("src/main/resources", "stopWords.inp"), StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.stopWords = stopWords;
    }

    public List<String> execute(final List<String> words) {
        return words.stream()
                .filter(word -> !stopWords.contains(word))
                .collect(Collectors.toList());
    }
}
