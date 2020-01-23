package homework.search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SearchApplication {

    public static void main (String [] arg) throws IOException {

        System.out.println("Введите тип запроса (index (1) или search (2)): ");
        String requestType = getString();
//        String requestType = "2";
        if ("1".equals(requestType) || "index".equals(requestType)){
            System.out.println("Файл для индекса: ");
            String indexFile = getString();
//            String indexFile = "E:/Java/IdeaProjects/index_file1.txt";//String indexFile = "D:/schoolHH/index_file1.txt";
            System.out.println("Файл с исходными данными: ");
            String inputFile = getString();
//            String inputFile = "E:/Java/IdeaProjects/input_file.txt";//String inputFile = "D:/schoolHH/input_file.txt";

            // вызываем сервис для обработки запроса 1
            final Indexer indexer = new Indexer(indexFile);
            indexer.writeIndex(inputFile);

//            final Service serviceWriteIndex = new Service();
//            serviceWriteIndex.writeIndexFile(inputFile, indexFile);
        }

        else if ("2".equals(requestType) || "search".equals(requestType)){
             System.out.println("Файл с индексом: ");
             String indexFile = getString();
//             String indexFile = "E:/Java/IdeaProjects/index_file.txt";//String indexFile = "D:/schoolHH/index_file1.txt";

             // вызываем сервис для обработки запроса 2
            final Searcher searcher = new Searcher(indexFile);
            System.out.println("Введите запрос или EXIT для выхода: ");
            String query = getString();
//            String query = "JAva".toLowerCase();
            while(!"exit".equals(query)) {
                searcher.analyzerSearch(query);
                System.out.println("Введите запрос: ");
                query = getString();
            }
        }
        else {
            System.out.println("Запрос неккоректен");
        }
    }

    public static String getString() throws IOException {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        return br.readLine().toLowerCase();
    }

}
