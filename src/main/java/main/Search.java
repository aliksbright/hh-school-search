package main;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.stream.Collectors;

import static main.Search.QUERY_KEYS.*;
import static main.Utils.*;


public class Search {
    private final ArrayList<String> queryPermutations = new ArrayList<>();
    private final ArrayList<String> EXCLUDED_WORDS = new ArrayList<>(Arrays.asList("в", "на", "работа", "and", "or", "not"));
    private final String EMPTY_RESULT = "";
    private Map<String, List<Long>> index;
    private RandomAccessFile docFile;
    private final String NOTHING_FOUND = "Ничего не нашлось :(";



    public void search(String indexPath, String docFilePath, String query, int limit) throws IOException {
        index = readIndexLong(indexPath);
        docFile = new RandomAccessFile(docFilePath, "rw");

        List<String> searched;
        switch (searchForKeywords(query)) {
            case AND:
                searched = searchWordsWithAnd(index, query);
                break;
            case OR:
                searched = searchWordsWithOr(index, query, limit);
                break;
            case NOT:
                searched = searchWordsWithNot(query);
                break;
            default:
                searched = searchOneWord(index, query);

        }
        System.out.println(searched.isEmpty() ? NOTHING_FOUND : searched);

    }

    private List<String> searchOneWord(Map<String, List<Long>> indexLong, String wordToFind) {
        return indexLong.getOrDefault(wordToFind, Collections.emptyList()).stream().map(aLong -> {
            try {
                docFile.seek(aLong);
                return docFile.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                return EMPTY_RESULT;
            }
        }).collect(Collectors.toList());
    }

    private List<String> searchWordsWithNot(String query) {
        ArrayList<String> queryWords = new ArrayList<>(Arrays.asList(query.split(" ")));
        List<String> originalWord = searchOneWord(index, queryWords.get(0));
        queryWords.remove(0);
        for (String excludedWord : queryWords) {
            originalWord.removeAll(searchOneWord(index, excludedWord));
        }
        return originalWord;
    }

    private List<String> searchWordsWithAnd(Map<String, List<Long>> index, String query) {
        final Set<List<Long>> eachWordDocs = Arrays.stream(query.split(" "))
                .map(String::toLowerCase)
                .filter(s -> !EXCLUDED_WORDS.contains(s))
                .filter(index::containsKey)
                .map(index::get)
                .collect(Collectors.toSet());
        return findCommonDocsLong(eachWordDocs).stream()
                .map(aLong -> {
                    try {
                        docFile.seek(aLong);
                        return docFile.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return EMPTY_RESULT;
                    }
                }).collect(Collectors.toList());

    }

    private List<String> searchWordsWithOr(Map<String, List<Long>> index, String query, Integer minWordsIncluded) {
        permuteIteration(queryPermutations, Arrays.stream(query.split(" "))
                .map(String::toLowerCase)
                .filter(s -> !EXCLUDED_WORDS.contains(s))
                .filter(index::containsKey)
                .distinct()
                .toArray(String[]::new), 0, minWordsIncluded);
        return queryPermutations.stream()
                .map(permutation -> searchWordsWithAnd(index, permutation))
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public enum QUERY_KEYS {
        AND,
        OR,
        NOT,
        NOKEYS
    }
}
