package main.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class UpdateIndex {
    private static Map<String, List<Long>> index;
    private static RandomAccessFile docFile;
    private static long positionInIndex;

    public static void addWordsToIndex(String indexPath, String fileToAddPath) throws IOException {
        index = Utils.readIndexLong(indexPath + "index.txt");
        docFile = new RandomAccessFile(indexPath + "doc_file.txt", "rw");
        positionInIndex = docFile.length();
        docFile.seek(positionInIndex);
        try (Stream<String> stream = Files.lines(Paths.get(fileToAddPath))) {
            stream
                    .peek(documentLine -> {

                        try {
                            saveDocument(documentLine);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    })
                    .peek(documentLine -> Utils.getWordsFromLine(documentLine)
                            .forEach(word -> updateIndexMapLong(positionInIndex, word)))
                    .forEach(s -> {
                        try {
                            positionInIndex = docFile.length();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });


            ObjectMapper objectMapper = new ObjectMapper();
            File indexFile = new File(indexPath + "index.txt");
            objectMapper.writeValue(indexFile, index);
        }
        docFile.close();
    }

    private static void saveDocument(String documentLine) throws IOException {
        docFile.seek(docFile.length());
        docFile.writeBytes(docFile.length() != 0 ? "\n" + documentLine : documentLine);
    }


    private static void updateIndexMapLong(Long linesIndex, String word) {
        if (!index.containsKey(word)) {
            index.put(word, new ArrayList<>());
        }
        index.get(word).add(linesIndex);
    }


}
