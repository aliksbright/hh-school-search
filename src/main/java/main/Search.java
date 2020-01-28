package main;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.stream.Collectors;

import static main.Utils.*;


public class Search {
    private static ArrayList<String> queryPermutations = new ArrayList<>();
    private static final ArrayList<String> EXCLUDED_WORDS = new ArrayList<>(Arrays.asList("в", "на", "работа", "and", "or", "not"));
    private static Map<String, List<Long>> index;
    private static RandomAccessFile docFile;


    public static void search(String indexPath, String query, int limit) throws IOException {
        index = readIndexLong(indexPath + "index.txt");
        docFile = new RandomAccessFile(indexPath + "doc_file.txt", "rw");

        List<String> searched;
        switch (searchForKeywords(query)) {
            case "and":
                searched = searchWordsWithAnd(index, query);
                break;
            case "or":
                searched = searchWordsWithOr(index, query, limit);
                break;
            case "not":
                searched = searchWordsWithNot(query);
                break;
            default:
                searched = searchOneWord(index, query);

        }
        System.out.println(searched.isEmpty() ? "Ничего не нашлось :(" : searched);

    }

    private static List<String> searchOneWord(Map<String, List<Long>> indexLong, String wordToFind) {
        return indexLong.getOrDefault(wordToFind, new ArrayList<>()).stream().map(aLong -> {
            try {
                docFile.seek(aLong + 1L);
                return docFile.readLine();

            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
        }).collect(Collectors.toList());
    }

    private static List<String> searchWordsWithNot(String query) {
        ArrayList<String> queryWords = new ArrayList<>(Arrays.asList(query.split(" ")));
        List<String> originalWord = searchOneWord(index, queryWords.get(0));
        queryWords.remove(0);
        for (String excludedWord : queryWords) {
            originalWord.removeAll(searchOneWord(index, excludedWord));
        }
        return originalWord;
    }

    private static List<String> searchWordsWithAnd(Map<String, List<Long>> index, String query) {
        final Set<List<Long>> eachWordDocs = Arrays.stream(query.split(" "))
                .map(String::toLowerCase)
                .filter(s -> !EXCLUDED_WORDS.contains(s))
                .filter(index::containsKey)
                .map(index::get)
                .collect(Collectors.toSet());
        return findCommonDocsLong(eachWordDocs).stream()
                .map(aLong -> {
                    try {
                        docFile.seek(aLong + 1L);
                        return docFile.readLine();

                    } catch (IOException e) {
                        e.printStackTrace();
                        return "";
                    }
                }).collect(Collectors.toList());

    }

    public static List<String> searchWordsWithOr(Map<String, List<Long>> index, String query, Integer minWordsIncluded) {
        permuteIteration(queryPermutations, Arrays.stream(query.split(" "))
                .map(String::toLowerCase)
                .filter(s -> !EXCLUDED_WORDS.contains(s))
                .filter(index::containsKey)
                .distinct()
                .toArray(String[]::new), 0, minWordsIncluded);
        return queryPermutations.stream()
                .map(permutation -> searchWordsWithAnd(Search.index, permutation))
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

}
