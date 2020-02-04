package ru.hh.search;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
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
     * Создатель XML-ки.
     */
    private final CreateXML createXML;

    /**
     *
     * @param writer - Writer для записи индекса. В конструктор Writer-а подается путь,
     *               куда будет записан индекс.
     */
    public Indexer(Writer writer) throws ParserConfigurationException {
        this.createXML = new CreateXML(writer);
    }

    /**
     * Находит все файлы в указанной папке, если указанный путь является папкой,
     * если нет, то вернет список с одним путем.
     * @return список файлов из указанной папки.
     */
    private List<Path> getFiles(Path pathToIndex) throws IOException {
        ArrayList<Path> paths = new ArrayList<>();
        paths.add(pathToIndex);
        if (pathToIndex.toFile().isDirectory()) {
            try (DirectoryStream<Path> str =
                         Files.newDirectoryStream(pathToIndex, path -> path.toFile().isFile())) {
                paths.remove(0);
                str.forEach(paths::add);
            }
        }
        return paths;
    }

    /**
     * Пробегается по все файлам из списка и индексирует каждую строчку, каждого файла.
     * Сохраняет индекс на диск.
     * @param paths список файлов.
     */
    private void createIndex(List<Path> paths) throws IOException, TransformerException {
        AtomicInteger docId = new AtomicInteger();
        AtomicInteger lineId = new AtomicInteger();
        for (Path path : paths) {
            try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
                reader.lines().forEach(line -> this.createXML.addElement(
                        String.valueOf(docId.incrementAndGet()),
                        String.valueOf(lineId.incrementAndGet()),
                        String.join("/", getTokens(line)))
                );
            }
        }
        this.createXML.save();
    }

    public void execute(Path pathToIndex) throws IOException, TransformerException {
        createIndex(getFiles(pathToIndex));
    }
}
