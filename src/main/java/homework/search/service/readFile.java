package homework.search.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class readFile {
    private  readFile() {
    }

    public static List<String> readInputFile(String inputFile) {
        try {
            return Files.readAllLines(Paths.get(inputFile), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

//    public static Set<String> readStopWords(String resourceName) {
//        try {
//            return new HashSet<>(Files.readAllLines(Paths.get(resourceName), StandardCharsets.UTF_8));
//        } catch (IOException e) {
//            e.printStackTrace();
//            return new HashSet<>();
//        }
//    }
}
