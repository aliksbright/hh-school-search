package app.search;

import app.structure.InvertedIndex;
import app.structure.Term;
import app.structure.TermInv;

import java.lang.reflect.Array;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Searcher {

    private String query;
    private HashMap<String, TermInv> invIndex;

    public Searcher(String query, HashMap<String, TermInv> invIndex) {
        this.query = query
                .replaceAll("\\s\\s", " ")
                .replaceAll("[.,/!?;:]", "");
        this.invIndex = invIndex;
    }

    public Set<Integer> getRequestedDocs() {
        Set<Integer> requestedDocs;
        if (query.matches("^\"(.)+\"$"))
            requestedDocs = phrase();
        else if (query.matches(".*[\\^\\s]NOT\\s[\\wA-Яа-я].*"))
            requestedDocs = not();
        else if (query.matches(".*\\sOR\\s.*"))
            requestedDocs = or();
        else
            requestedDocs = termAndMinor();
        return requestedDocs;
    }

    private Set<Integer> phrase() {
        List<String> queryList = List.of(query.replaceAll("\"", "").split("\\s"));
        return null;
    }

    // Term-query, AND-query, MIN OR-query
    private Set<Integer> termAndMinor() {
        HashMap<Integer, Integer> docIdCountMap = new HashMap<>();

        String[] queryArray;
        int minIn;

        Pattern pattern = Pattern.compile("~\\d+");
        Matcher matcher = pattern.matcher(query);
        if (matcher.find()) {
            queryArray = query.replaceFirst("~\\d+", "").split("\\sOR\\s");
            minIn = Integer.parseInt(matcher.group(0).replaceFirst("~", ""));
        } else if (query.matches("\\sAND\\s")) {
            queryArray = query.split("\\sAND\\s");
            minIn = queryArray.length;
        } else {
            queryArray = query.split("\\s");
            minIn = queryArray.length;
        }

        Arrays.stream(queryArray)
                .map(s -> invIndex.get(s.trim().toLowerCase()))
                .filter(Objects::nonNull)
                .flatMap(s1 -> Set.copyOf(s1.getDocIds()).stream())
                .forEach(id -> docIdCountMap.put(id, docIdCountMap.getOrDefault(id, 0) + 1));

        return docIdCountMap.keySet().stream()
                .filter(id -> docIdCountMap.get(id) == minIn)
                .collect(Collectors.toSet());
    } // TODO refac... it

    private Set<Integer> not() {
        String regex = "[\\^\\s]NOT\\s[\\wA-Яа-я]+";
        String toReplace = "[\\^\\s]NOT\\s";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(query);
        matcher.find();
        String minusWord = matcher.group(0).replaceAll(toReplace, "");
        String subQuery = query.replaceFirst(regex, "");

        Searcher searcher = new Searcher(subQuery, invIndex);
        Searcher searcherExclude = new Searcher(minusWord, invIndex);

        Set<Integer> docs = searcher.getRequestedDocs();
        docs.removeAll(searcherExclude.getRequestedDocs());

        return docs;
    }

    private Set<Integer> or() {
        return Arrays.stream(query.split("\\sOR\\s"))
                .map(word -> invIndex.get(word.trim().toLowerCase()))
                .filter(Objects::nonNull)
                .flatMap(termInv -> termInv.getDocIds().stream())
                .collect(Collectors.toSet());
    }

}
