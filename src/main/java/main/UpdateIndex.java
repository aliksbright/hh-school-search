package main;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static main.Utils.getWordsFromLine;
import static main.Utils.readIndexLong;

public class UpdateIndex {
    private Map<String, List<Long>> index;
    private RandomAccessFile docFile;
    private long positionInIndex;

    public void addWordsToIndex(String indexPath, String docFilePath, String fileToAddPath) throws IOException {
        index = readIndexLong(indexPath);
        docFile = new RandomAccessFile(docFilePath, "rw");
        positionInIndex = docFile.length();
        docFile.seek(positionInIndex);
        try (Stream<String> stream = Files.lines(Paths.get(fileToAddPath))) {
            stream
                    .forEach(documentLine -> {
                        try {
                            saveDocumentToFile(documentLine, docFile);
                            getWordsFromLine(documentLine)
                                    .forEach(word -> updateIndexMap(positionInIndex, word, index));
                            positionInIndex = docFile.length();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

            ObjectMapper objectMapper = new ObjectMapper();
            File indexFile = new File(indexPath);
            objectMapper.writeValue(indexFile, index);
        }
        docFile.close();
    }

    private void saveDocumentToFile(String documentLine, RandomAccessFile docFile) throws IOException {
        docFile.seek(docFile.length());
        docFile.writeBytes(documentLine + "\n");
    }


    private void updateIndexMap(Long linesIndex, String word, Map<String, List<Long>> index) {
        if (!index.containsKey(word)) {
            index.put(word, new ArrayList<>());
        }
        index.get(word).add(linesIndex);
    }


}
