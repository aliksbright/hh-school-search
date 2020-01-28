package main;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Utils {


    private static StringBuilder sb = new StringBuilder();

    public static Map<String, List<Long>> readIndexLong(String indexPath) throws IOException {
        File file = new File(indexPath);
        if (file.length() != 0) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            try {
                return objectMapper.readValue(file, new TypeReference<Map<String, List<Long>>>() {
                });
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
        return new HashMap<>();
    }


    public static void permuteIteration(ArrayList<String> queryPermutations, String[] arr, int index, int limit) {
        if (index >= limit) {
            for (int i = 0; i < limit; i++) {
                sb.append(arr[i]).append(" ");
            }
            queryPermutations.add(sb.toString());
            sb.delete(0, sb.length());
            return;
        }

        for (int i = index; i < arr.length; i++) {
            String temp = arr[index];
            arr[index] = arr[i];
            arr[i] = temp;

            permuteIteration(queryPermutations, arr, index + 1, limit);

            temp = arr[index];
            arr[index] = arr[i];
            arr[i] = temp;
        }
    }


    public static List<Long> findCommonDocsLong(Set<List<Long>> eachWordDocs) {
        boolean first = true;
        ArrayList<Long> uniqueDocs = new ArrayList<>();
        for (List<Long> list : eachWordDocs) {
            if (first) {
                uniqueDocs.addAll(list);
                first = false;
            } else {
                uniqueDocs.retainAll(list);
            }
        }
        return uniqueDocs;
    }


    public static Search.QUERY_KEYS searchForKeywords(String query) {
        for (String keyWord : query.split(" ")) {
            try {
                return Search.QUERY_KEYS.valueOf(keyWord.toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }
        return (query.split(" ").length > 1 ? Search.QUERY_KEYS.OR : Search.QUERY_KEYS.NOKEYS);

    }

    public static Set<String> getWordsFromLine(String docLine) {
        return Arrays.stream(docLine.split(" "))
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }
}
