package homework.search;

import homework.search.service.IndexBuilder;
import homework.search.service.IndexSearcher;
import homework.search.service.StopWordsAnalysis;
import homework.search.service.Tokenizer;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.valueOf;

public class Searcher {
    private final IndexSearcher searcher;
    private static Tokenizer tokenizer;
    private static StopWordsAnalysis stopWordsAnalysis;

    public Searcher(String filePath){
        new IndexBuilder(filePath);
        searcher = new IndexSearcher(IndexBuilder.readIndex());
        tokenizer = new Tokenizer();
        stopWordsAnalysis = new StopWordsAnalysis();
    }


    public List<Integer> searchDocs(String query){
        List<Integer> documents;
        if(valueOf(query.charAt(0)).equals("\"") &&
                valueOf(query.charAt(query.length()-1)).equals("\"")){
            List<String> terms = getTerms(query.substring(1,query.length()-1));
            documents = getDocIdsByPhrase(terms);
        }
        else {
            List<String> terms = getTerms(query);
            documents = getDocIds(terms);
        }
        return documents;
    }


    public List<Integer> getDocIds(List<String> queries) {
        return searcher.getDocIds(queries);
    }

    public List<Integer> getDocIdsByPhrase(List<String> queries) {
        return searcher.getDocIdsByPhrase(queries);
    }

    public void printAnswer(String query, List<Integer> docIds) {
        if (docIds.isEmpty()) {
            System.out.println("По запросу " + query.toUpperCase() + " ничего не найдено :(");
        } else {
            System.out.println("По запросу " + query.toUpperCase() + " найдены документы:");
            System.out.println(getStringFromList(docIds));
        }
    }

    private String getStringFromList(List<Integer> docIds) {
        return docIds.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));
    }

    private List<String> getTerms(String text) {
        return stopWordsAnalysis.execute(tokenizer.getTerms(text));
    }
}
