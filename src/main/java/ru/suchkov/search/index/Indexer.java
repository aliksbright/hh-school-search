package ru.suchkov.search.index;

import com.google.gson.Gson;
import ru.suchkov.search.model.Document;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * Класс индексатора
 */
public class Indexer {

    private Path indexDirectory;
    private Path indexedFile;
    private Matrix matrix;
    private Dictionary wordDictionary;
    private TextLemmatizer lemmatizer;

    public Indexer(Path indexDirectory, Path indexedFile) {
        this.indexDirectory = indexDirectory;
        this.indexedFile = indexedFile;
        this.matrix = new Matrix();
        this.wordDictionary = new Dictionary();
        lemmatizer = new TextLemmatizer();
    }

    /**
     * строит индекс
     */
    public void build() {
        AtomicInteger docCounter = new AtomicInteger(0);
        try(Stream<String> lines = Files.lines(indexedFile)) {
            lines.map(line -> new Gson().fromJson(line, Document.class)).forEach(document -> {
                int documentId = docCounter.incrementAndGet();
                Stream.of(TextNormalizer.normalize(document.getText())).forEach(word -> {
                    String lemmatized = lemmatizer.lemmatize(word);
                    wordDictionary.add(lemmatized);
                    matrix.add(wordDictionary.getKey(lemmatized), documentId);
                });
            });
            wordDictionary.save(indexDirectory);
            matrix.save(indexDirectory);
            System.out.println("Индекс успешно создан");
        } catch (IOException e) {
            System.out.println("Индекс не построен. Проерьте, что указанные файлы и директории существуют, " +
                    "а данные валидны");
        } catch (Exception e) {
            System.out.println("Индекс не построен по непредвиденной ошибке");
        }
    }

}
