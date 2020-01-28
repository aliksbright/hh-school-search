package homework.search.service;

import homework.search.object.TermPosition;

import java.util.*;
import java.util.stream.Collectors;

public class IndexSearcher {
    private static HashMap<String, List<TermPosition>> indexData;

    public IndexSearcher(HashMap<String, List<TermPosition>> indexData) {
        IndexSearcher.indexData = indexData;
    }
    
    //фразовый запрос. в ответе документы, которые содержат все слова фразы
    public List<Integer> getDocIdsByPhrase(List<String> queries) {
        if (!queries.isEmpty()) {
            return getDocIdsByPhrase1(queries);
        }
        else
            return Collections.EMPTY_LIST;
    }

    private List<Integer> getDocIdsByPhrase1(List<String> queries) {
        Set<TermPosition> documents1 = new HashSet<>(getIndexElementsByTerm(queries.get(0)));
        if (queries.size()>1) {
            for (int j = 1; j < queries.size(); j++) {
                Set<TermPosition> documents2 = new HashSet<>(getIndexElementsByTerm(queries.get(j)));

                documents1.retainAll(documents2);
                documents2.retainAll(documents1);

                Iterator<TermPosition> docIter1 = documents1.iterator();
                Iterator<TermPosition> docIter2 = documents2.iterator();
                removeElementsWhereNotPhrase(docIter1, docIter2);

                documents1 = documents2;
            }
        }
        return documents1.stream()
                .map(TermPosition::getDocId)
                .collect(Collectors.toList());
    }

    //удаление из набора данных, где нет фразы целиком. На вход подаются два итератора по коллекциям одинаковой размерности
    private void removeElementsWhereNotPhrase(Iterator<TermPosition> docIter1, Iterator<TermPosition> docIter2) {
        while (docIter1.hasNext()){
            TermPosition elem1 = docIter1.next();
            TermPosition elem2 = docIter2.next();
            if (!checkPhraseInDoc(elem1, elem2)) {
                docIter2.remove();
            }
        }
    }

    //возвращает список документов, поддерживаются булевые операторы AND и NOT.
    public List<Integer> getDocIds(List<String> queries) {
        ListIterator<String> listIterator = queries.listIterator();
        Set<Integer> removeDocs = new HashSet<>();
        Set<Integer> answerDocs = new HashSet<>();

        while(listIterator.hasNext()){
            String query = listIterator.next();
            if ("not".equals(query) && listIterator.hasNext()){
                removeDocs.addAll(getDocIdsByTerm(listIterator.next()));
            }
            else if("and".equals(query) && listIterator.hasNext()) {
                answerDocs.retainAll(getDocIdsByTerm(listIterator.next()));
            }
            else {
                answerDocs.addAll(getDocIdsByTerm(query));
            }
        }
        answerDocs.removeAll(removeDocs);
        return new ArrayList<>(answerDocs);
    }

    //поиск по одному слову в индексе
    //возвращает список TermPosition для одного слова из запроса
    private List<TermPosition> getIndexElementsByTerm(String term) {
        if(indexData.containsKey(term)){
            return new ArrayList<>(indexData.get(term));
        }
        else return Collections.EMPTY_LIST;
    }

    //поиск по одному слову в индексе
    //возвращает список docId для одного слова из запроса
    private List<Integer> getDocIdsByTerm(String term) {
        if(indexData.containsKey(term)){
            return indexData.get(term).stream()
                    .map(TermPosition::getDocId)
                    .collect(Collectors.toList());
        }
        else return Collections.EMPTY_LIST;
    }

    //проверка, что два элемента из запроса принадлежат одной фразе в документе
    private boolean checkPhraseInDoc(TermPosition tp1, TermPosition tp2) {
        for(Integer termPos1: tp1.getTermPositions()){
            for(Integer termPos2: tp2.getTermPositions()){
                if (termPos2-termPos1==1) return true;
            }
        }
        return false;
    }
}
