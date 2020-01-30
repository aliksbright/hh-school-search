package Searcher;

import Parser.ParseIndex;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Search {

    private final BufferedReader bufferedReader;

    public Search(BufferedReader bufferedReader) throws Exception {
        this.bufferedReader = bufferedReader;
        searchTerm();
    }

   private void searchTerm() throws Exception {
        System.out.println("Введите расположение индекса: ");
        String indexDirectoryPath = bufferedReader.readLine();
        System.out.println("Введите запрос: ");
        String queryString = bufferedReader.readLine();

        Path directoryIndexPath = Paths.get(indexDirectoryPath);
        Path fileIndexPath;
        queryString = queryString.toLowerCase();

        if (Files.isRegularFile(directoryIndexPath))
           fileIndexPath = directoryIndexPath;
        else if (Files.isDirectory(directoryIndexPath))
           fileIndexPath = directoryIndexPath.resolve("index.txt");
        else fileIndexPath = directoryIndexPath;

        if (!Files.exists(fileIndexPath) || !Files.isRegularFile(fileIndexPath)) {
           System.out.println("Файл индекса не найден");
           return;
        }

        ParseIndex indexFile = new ParseIndex(fileIndexPath);
        LinkedHashMap<String, ArrayList<String>> documentIndex = indexFile.getDocument();

        for(Map.Entry<String, ArrayList<String>> entry : documentIndex.entrySet()){
            if (entry.getKey().contains(queryString)) {
                System.out.println("Найденные файлы: ");
                for(String str : entry.getValue()){
                    System.out.println("* " + str);
                }
                return;
            }
        }
       System.out.println("Нет файлов, удовлетворяющих данному запросу");
    }
}
