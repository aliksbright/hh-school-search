package pisarev.index;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Index {
    private Path indexFile;
    private Path originalFile;
    private List<String> allLines;

    public Index(String indexFile, String originalFile) throws IOException {
        this.indexFile = Path.of(indexFile);
        this.originalFile = Path.of(originalFile);
        allLines = Files.lines(this.originalFile)
                .filter(str -> !str.equals(""))
                .map(line -> line.trim().replaceAll("[\\W\\s]+", " "))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Path getIndexFile() {
        return indexFile;
    }

    public Path getOriginalFile() {
        return originalFile;
    }

    public List<String> getAllLines() {
        return allLines;
    }

    public static void main(String[] args) throws IOException {
        Index index = new Index(args[0], args[1]);
        index.getAllLines().forEach(System.out::println);
    }
}
