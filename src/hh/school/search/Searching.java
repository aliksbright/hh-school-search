package hh.school.search;

import hh.school.additional.*;

import java.util.*;
import java.util.stream.Collectors;

public class Searching {
    private Map<String, Map<Integer, Integer>> index;
    private Boolean hasBooleanTerm = false;

    public Searching(String indexPath, String searchQuery) {
        String saveDirectory = indexPath.substring(0, indexPath.lastIndexOf("/"));
        Terms terms = new Terms(saveDirectory, false);

        Documents docs = new Documents(saveDirectory, false);

        this.index = terms.getIndex();

        Map<String, String> tokens = tokenizing(searchQuery);
        tokens = filtering(tokens);
        tokens = stemming(tokens);
        tokens.size();
        LinkedHashMap<Integer, Integer> sortingMap = new LinkedHashMap<>();
        if (hasBooleanTerm || (tokens.size() == 1)) sortingMap = getDocIdsForTokens(tokens);
        else sortingMap = getIdsForPhrase(tokens);

        for (Map.Entry<Integer, Integer> entry: sortingMap.entrySet()) {
            System.out.println(docs.getDocsById(entry.getKey()));
        }

    }

    private Map<String, String> tokenizing(String string) {
        List<String> words = Arrays.asList(string.split("\\b"));
        Punctuation punctuation = Punctuation.getInstance();
        words = words.stream()
                .map(word -> word.trim())
                .filter(word -> !punctuation.getPunctuation().contains(word))
                .collect(Collectors.toList());
        Map<String, String> resultMap = new LinkedHashMap<>();
        Integer count = 0;
        while (count < words.size()) {
            switch (words.get(count)) {
                case "NOT" :
                    if (count + 1 < words.size()) {
                        resultMap.put(words.get(count + 1), "NOT");
                        count += 2;
                    }
                    hasBooleanTerm = true;
                    break;
                case "AND" :
                case "OR" :
                    if ((count - 1 > -1) && (count + 1 < words.size())) {
                        resultMap.put(words.get(count + 1), "");
                        resultMap.put(words.get(count - 1), "AND");
                        count += 2;
                    }
                    hasBooleanTerm = true;
                    break;
                default:
                    resultMap.put(words.get(count), "");
                    count++;
                    break;
            }

        }
        return resultMap;
    }

    private Map<String, String> filtering(Map <String, String> tokens) {
        StopWords stopWords = StopWords.getInstance();
        return tokens.entrySet().stream()
                .filter(entry -> !stopWords.getStopWords().contains(entry.getKey()))
                .collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        entry -> entry.getValue(),
                        (oldValue, newValue) -> newValue,
                        LinkedHashMap::new
                ));
    }

    private Map<String, String> stemming(Map<String, String> tokens) {
        PorterStem stem = new PorterStem();
        return tokens.entrySet().stream()
                .collect(Collectors.toMap(
                    entry -> stem.stem(entry.getKey()),
                    entry -> entry.getValue(),
                    (oldValue, newValue) -> newValue,
                    LinkedHashMap::new
                ));
    }

    private LinkedHashMap<Integer, Integer> getIdsForPhrase(Map<String, String> tokens) {
        Map<Integer, Integer> result = new HashMap<>();
        String previousTerm = "";
        for (Map.Entry<String, String> token: tokens.entrySet()) {
            if (index.containsKey(token.getKey())) {
                Map<Integer, Integer> docIds = index.get(token.getKey());
                if ((previousTerm != "") && (index.containsKey(token.getKey()))) {
                    Map <Integer, Integer> prevDocIds = index.get(previousTerm);
                    for (Map.Entry<Integer, Integer> entry : docIds.entrySet()) {
                        if (prevDocIds.containsKey(entry.getKey())) {
                            if (entry.getValue() - prevDocIds.get(entry.getKey()) == 1)
                                if (result.containsKey(entry.getKey()))
                                    result.put(entry.getKey(), result.get(entry.getKey()) + 1);
                                else result.put(entry.getKey(), 1);
                        }
                    }
                }
            }
            previousTerm = token.getKey();
        }

        return sortingMap(result);
    }

    private LinkedHashMap<Integer, Integer> getDocIdsForTokens(Map<String, String> tokens) {
        Map<Integer, Integer> notDocIds = new HashMap<>();
        Map<Integer, Integer> andDocIds = new HashMap<>();
        Map<Integer, Integer> simpleDocIds = new HashMap<>();
        for (Iterator<Map.Entry<String,String>> it = tokens.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String,String> term = it.next();
            switch (term.getValue()) {
                case "NOT" :
                    notDocIds.putAll(simpleDocIds(term.getKey()));
                    break;
                case "AND" :
                    if (it.hasNext()) {
                        andDocIds.putAll(andDocIds(term.getKey(), it.next().getKey()));
                    } else {
                        simpleDocIds.putAll(simpleDocIds(term.getKey()));
                    }
                    break;
                default:
                    simpleDocIds.putAll(simpleDocIds(term.getKey()));
            }
        }

        andDocIds.forEach((andKey, andValue) ->
                simpleDocIds.merge(andKey, andValue, (andVal, simpleVal) ->
                        andVal + simpleVal));

        simpleDocIds.keySet().removeAll(notDocIds.keySet());

       return sortingMap(simpleDocIds);
    }

    private Map<Integer, Integer> simpleDocIds(String term) {
        Map<Integer, Integer> resultMap = new HashMap<>();
        if (index.containsKey(term)) {
            for (Map.Entry<Integer, Integer> docId: index.get(term).entrySet()) {
                if (resultMap.containsKey(docId.getKey())) {
                    resultMap.put(docId.getKey(), resultMap.get(docId.getKey()) + 1);
                } else {
                    resultMap.put(docId.getKey(), 1);
                }
            }
        }
        return resultMap;
    }

    private Map<Integer, Integer> andDocIds(String term1, String term2) {
        Map<Integer, Integer> resultMap;
        if (index.containsKey(term1) && index.containsKey(term2)) {
            Map<Integer, Integer> term1Map = simpleDocIds(term1);
            Map<Integer, Integer> term2Map = simpleDocIds(term2);
            resultMap = new HashMap<>(term1Map);
            resultMap.keySet().retainAll(term2Map.keySet());
        } else {
            if (index.containsKey(term1)) resultMap = simpleDocIds(term1);
            else if (index.containsKey(term2)) resultMap = simpleDocIds(term2);
                else resultMap = new HashMap<>();
        }
        return resultMap;
    }

    private LinkedHashMap<Integer, Integer> sortingMap(Map<Integer, Integer> m) {
        List<Map.Entry<Integer, Integer>> bufferList = m.entrySet().stream()
                .collect(Collectors.toList());

        Comparator<Map.Entry<Integer, Integer>> mapComparator = new Comparator<Map.Entry<Integer, Integer>>() {
            @Override
            public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                Integer value1 = o1.getValue();
                Integer value2 = o2.getValue();
                return value1.compareTo(value2);
            }
        };

        return bufferList.stream()
                .sorted(mapComparator.reversed())
                .collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        entry -> entry.getValue(),
                        (oldValue, newValue) -> newValue,
                        LinkedHashMap::new
                ));
    }


}
