package Indexer;

import Parser.ParseFile;
import Parser.ParseIndex;
import Writer.Write;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class BuildIndex {

    private final BufferedReader bufferedReader;

    public BuildIndex(BufferedReader bufferedReader) throws Exception {
        this.bufferedReader = bufferedReader;
        buildIndex();
    }

    private void buildIndex() throws Exception {
        System.out.println("Введите расположение индекса: ");
        String stringIndexDirectoryPath = bufferedReader.readLine();
        System.out.println("Введите расположение индексируемого файла: ");
        String stringFilePath = bufferedReader.readLine();

        Path directoryIndexPath = Paths.get(stringIndexDirectoryPath);
        Path filePath = Paths.get(stringFilePath);

        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            System.out.println("Нет такого файла");
            return;
        }

        Path fileIndexPath;

        if (!Files.exists(directoryIndexPath))
            Files.createDirectories(directoryIndexPath);
        if (Files.isRegularFile(directoryIndexPath))
            fileIndexPath = directoryIndexPath;
        else if (Files.isDirectory(directoryIndexPath))
            fileIndexPath = directoryIndexPath.resolve("index.txt");
        else fileIndexPath = directoryIndexPath;

        if (!Files.exists(fileIndexPath))
            Files.createFile(fileIndexPath);

        LinkedHashMap<String, ArrayList<String>> documentIndex = addTerms(fileIndexPath, filePath);
        new Write(fileIndexPath, documentIndex);
        System.out.println("Файл добавлен в индекс\n");
    }

    private LinkedHashMap<String, ArrayList<String>> addTerms(Path fileIndexPath, Path filePath) throws Exception {
        ParseIndex indexFile = new ParseIndex(fileIndexPath);
        ParseFile file = new ParseFile(filePath);
        LinkedHashMap<String, ArrayList<String>> documentIndex = indexFile.getDocument();
        HashSet<String> documentFile = file.getDocument();
        String filename = filePath.getFileName().toString();
        for(String str : documentFile){
            ArrayList<String> list;
            if (documentIndex.containsKey(str))
                list = documentIndex.get(str);
            else
                list = new ArrayList<>();
            list.add(filename);
            documentIndex.put(str, list);
        }
        return documentIndex;
    }
}
