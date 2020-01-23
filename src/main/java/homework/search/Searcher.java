package homework.search;

import homework.search.service.IndexSearcher;
import homework.search.service.Indexing;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.valueOf;

public class Searcher {
    private final IndexSearcher searcher;
    private final Indexing indexing;

    public Searcher(String filePath){
        indexing = new Indexing(filePath);
        searcher = new IndexSearcher(indexing.readIndex());
    }


    public void analyzerSearch (String query){
        List<Integer> documents;
        if(valueOf(query.charAt(0)).equals("\"") &&
                valueOf(query.charAt(query.length()-1)).equals("\"")){
            List<String> queryArr = Arrays.asList(query.substring(1,query.length()-1).split("[.,:;!?\\s]+"));
            documents = searchByPhrase(queryArr);
        }
        else {
            List<String> queryArr = Arrays.asList(query.split("[.,:;!?\\s]+"));
            documents = searchDoc(queryArr);
        }
        printAnswer(query, documents);
    }


    public List<Integer> searchDoc (List<String> queryArr) {
        return searcher.searchDocs(queryArr);
    }

    public List<Integer> searchByPhrase(List<String> queryArr) {
        return searcher.searchDocsByPhrase(queryArr);
    }

    private void printAnswer(String query, List<Integer> documents) {
        if (!documents.isEmpty()) {
//            System.out.println("По запросу " + "\u001B[1m" + query.toUpperCase() + "\u001b[0m" + " найдены документы:");
            System.out.println("По запросу " + query.toUpperCase() + " найдены документы:");
            System.out.println(getStringFromList(documents));
        } else {
//            System.out.println("По запросу " + "\u001B[1m" + query.toUpperCase() + "\u001b[0m" + " ничего не найдено :(");
            System.out.println("По запросу " + query.toUpperCase() + " ничего не найдено :(");
        }
    }

    private String getStringFromList(List<Integer> answer) {
        return answer.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));
    }
}
