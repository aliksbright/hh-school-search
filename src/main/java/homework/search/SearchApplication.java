package homework.search;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class SearchApplication {

    public static void main (String [] arg) {
        System.out.println("Введите тип запроса:\n" +
                "1 (index) - создать индекс;\n" +
                "2 (search) - поиск по индексу;\n" +
                "3 (q) - выход из программы.");
        String requestType = getString();

        if ("1".equals(requestType) || "index".equals(requestType)){
            System.out.println("Файл для индекса: ");
            String indexFile = getString();
//            String indexFile = "D:/schoolHH/index.txt";//String indexFile = "E:/Java/IdeaProjects/index.txt";//
            System.out.println("Файл с исходными данными: ");
            String inputFile = getString();
//            String inputFile = "D:/schoolHH/input_file.txt"; //String inputFile = "E:/Java/IdeaProjects/input_file.txt";
            inputFile = repeatInput(inputFile);

            final Indexer indexer = new Indexer(indexFile);
            indexer.writeIndex(inputFile);
        }

        else if ("2".equals(requestType) || "search".equals(requestType)){
             System.out.println("Файл с индексом: ");
             String indexFile = getString();
//            String indexFile = "D:/schoolHH/index.txt";//String indexFile = "E:/Java/IdeaProjects/index.txt";//
            indexFile = repeatInput(indexFile);
            final Searcher searcher = new Searcher(indexFile);
            System.out.println("Введите запрос или \"Q\" для выхода: ");
            String query = getString();
            while(!"q".equals(query)) {
                List<Integer> docId = searcher.searchDocs(query);
                searcher.printAnswer(query, docId);
                System.out.println("Введите запрос или \"Q\" для выхода: ");
                query = getString();
            }
        }
        else if ("3".equals(requestType) || "q".equals(requestType)){
            System.out.println("Выход из программы");
        }
        else {
            System.out.println("Запрос неккоректен, работа программы завершена!");
        }
    }

    public static String getString() {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        try {
            return br.readLine().toLowerCase();
        } catch (IOException e) {
            System.out.println("Данные не считаны");
            e.getMessage();
        }
        return "";
    }

    private static String repeatInput(String input) {
        while (!checkFile(input)) {
            System.out.println("Указанный файл не найден, попробуйте снова:");
            input = getString();
        }
        return input;
    }

    public static boolean checkFile(String filePath) {
        return new File(filePath).exists();
    }
}
