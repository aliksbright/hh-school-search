package homework.search.service;

import java.util.*;

public class IndexSearcher {
    private static HashMap<String, List<Integer>> indexData;

    public IndexSearcher(HashMap<String, List<Integer>> indexData) {
        this.indexData = indexData;
    }
    
    //фразовый запрос. в ответе документы, которые содержат все слова фразы
    public List<Integer> searchDocsByPhrase(List<String> queries) {
        Set<Integer> documents = new HashSet<>(searchDocsByWord(queries.get(0)));
        for(String query: queries){
            documents.retainAll(searchDocsByWord(query));
        }
        return new ArrayList<>(documents);
    }
    //результат с AND и NOT.
    //AND не может быть первым в строке, используем список по первому слову
    public List<Integer> searchDocs(List<String> queries) {
        ListIterator<String> listIterator = queries.listIterator();
        Set<Integer> removeDocs = new HashSet<>();;
        Set<Integer> answerDocs = new HashSet<>();

        while(listIterator.hasNext()){
            String query = listIterator.next();
            if ("not".equals(query) && listIterator.hasNext()){
                removeDocs.addAll(searchDocsByWord(listIterator.next()));
            }
            else if("and".equals(query) && listIterator.hasNext()) {
                answerDocs.retainAll(searchDocsByWord(listIterator.next()));
            }
            else {
                answerDocs.addAll(searchDocsByWord(query));
            }
        }
        answerDocs.removeAll(removeDocs);
        return new ArrayList<>(answerDocs);
    }

    //поиск по одному слову в индексе
    protected List<Integer> searchDocsByWord(String query) {
        return indexData.getOrDefault(query, Collections.EMPTY_LIST);
    }
}
