package index;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Normalizator {

    public static ArrayList<String> normalizeStrings(Path file) throws IOException {
        return Files.lines(file)
                .filter(str -> !str.equals(""))
                .map(line -> line.replaceAll("[^\\d\\wа-яА-ЯёЁ\\s]", "")
                .trim().replaceAll("\\s++", " ").toLowerCase())
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
