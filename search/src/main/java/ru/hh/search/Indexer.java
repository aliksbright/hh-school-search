package ru.hh.search;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.hh.search.Util.getTokens;

/**
 * Индексатор.
 * @author Andrey Bukhtoyarov (andreymedoed@gmail.com).
 * @version %Id%.
 * @since 0.1.
 */
public class Indexer {
    /**
     * Путь (папка или файл) с файлами (файлом) для индексации.
     */
    private final Path pathToIndex;
    /**
     * Создатель XML-ки.
     */
    private final CreateXML createXML;

    /**
     *
     * @param pathToIndex - Путь (папка или файл) с файлами (файлом) для индексации.
     * @param writer - Writer для записи индекса. В конструктор Writer-а подается путь,
     *               куда будет записан индекс.
     */
    public Indexer(Path pathToIndex, PrintWriter writer) {
        this.createXML = new CreateXML(writer);
        this.pathToIndex = pathToIndex;
    }

    /**
     * Находит все файлы в указанной папке.
     * @return список файлов из указанной папки.
     */
    private List<Path> getFiles() {
        ArrayList<Path> paths = new ArrayList<>();
        try (DirectoryStream<Path> str = Files.newDirectoryStream(
                this.pathToIndex, path -> path.toFile().isFile())) {
            str.forEach(paths::add);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return paths;
    }

    /**
     * Пробегается по все файлам из списка и индексирует каждую строчку, каждого файла.
     * @param paths список файлов.
     */
    private void createIndex(List<Path> paths) {
        AtomicInteger docId = new AtomicInteger();
        AtomicInteger lineId = new AtomicInteger();
        paths.forEach(path -> {
            try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
                reader.lines().forEach(line -> this.createXML.addElement(
                        String.valueOf(docId.incrementAndGet()),
                        String.valueOf(lineId.incrementAndGet()),
                        String.join("/", getTokens(line)))
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Записывает индекс на диск.
     */
    private void saveIndex() {
        this.createXML.print();
    }

    public void execute() {
        if (pathToIndex.toFile().isDirectory()) {
            createIndex(getFiles());
            saveIndex();
        } else if (pathToIndex.toFile().isFile()) {
            createIndex(List.of(this.pathToIndex));
            saveIndex();
        }
    }
}
