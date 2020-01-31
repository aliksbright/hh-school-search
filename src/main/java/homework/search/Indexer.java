package homework.search;

import homework.search.object.Document;
import homework.search.service.IndexBuilder;
import homework.search.service.StopWordsAnalysis;
import homework.search.service.Tokenizer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Indexer {
    private final IndexBuilder indexBuilder;
    private static StopWordsAnalysis stopWordsAnalysis;
    private static Tokenizer tokenizer;

    public Indexer(String filePath) {
        indexBuilder = new IndexBuilder(filePath);
        tokenizer = new Tokenizer();
        stopWordsAnalysis = new StopWordsAnalysis();
    }

    public void writeIndex(String inputFile) {
        IndexBuilder.writeIndex(getDocs(inputFile));
    }

    public static List<Document> getDocs(String inputFile) {
        AtomicInteger docId = new AtomicInteger();
        try (Stream<String> stream = Files.lines(Paths.get(inputFile), StandardCharsets.UTF_8)) {
            return stream.map(doc -> new Document(docId.incrementAndGet(), getTerms(doc)))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return Collections.EMPTY_LIST;
    }

    private static List<String> getTerms(String text) {
        return stopWordsAnalysis.execute(tokenizer.getTerms(text));
    }
}


