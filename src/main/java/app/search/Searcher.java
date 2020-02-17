package app.search;

import app.structure.InvertedIndex;
import app.structure.Term;
import app.structure.TermInv;
import app.util.RegEx;

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
                .replaceAll(RegEx.SPACE + RegEx.SPACE, " ")
                .replaceAll(RegEx.PUNCTUATION, "");
        this.invIndex = invIndex;
    }

    public Set<Integer> getRequestedDocs() {
        Set<Integer> requestedDocs;
        if (query.matches("^\"(.)+\"$"))
            requestedDocs = phrase();
        else if (query.matches(".*[\\^\\s]NOT\\s[\\wA-Яа-я].*"))
            requestedDocs = not();
        else if (query.matches(".*" + RegEx.OR + ".*"))
            requestedDocs = or();
        else
            requestedDocs = termAndMinor();
        return requestedDocs;
    }

    private Set<Integer> phrase() {
        Set<Integer> requestedDocs = new HashSet<>();
        String[] listQuery = query.replaceAll("\"", "").split(RegEx.SPACE);
        for (int i = 0; i < listQuery.length - 1; i++) {
            HashMap<Integer, Set<Integer>> map = new HashMap<>();
            List<Integer> docIds = invIndex.getOrDefault(listQuery[i], new TermInv(-1, -1)).getDocIds();
            List<Integer> positionsInDoc = invIndex.getOrDefault(listQuery[i], new TermInv(-1, -1)).getPositionInDoc();
            for (int j = 0; j < docIds.size(); j++) {
                Set<Integer> pos = map.get(docIds.get(j));
                if (pos == null) {
                    pos = new HashSet<>();
                    pos.add(positionsInDoc.get(j));
                    map.put(docIds.get(j), pos);
                } else {
                    pos.add(positionsInDoc.get(j));
                }
            }

            List<Integer> nextDocIds = invIndex.getOrDefault(listQuery[i + 1], new TermInv(-10, -1)).getDocIds();
            List<Integer> nextPositionsInDoc = invIndex.getOrDefault(listQuery[i + 1], new TermInv(-10, -1)).getPositionInDoc();
            for (int j = 0; j < nextDocIds.size(); j++) {
                Set<Integer> pos = map.get(nextDocIds.get(j));
                if (pos != null) {
                    if (pos.contains(nextPositionsInDoc.get(j) - 1) ||
                            pos.contains(nextPositionsInDoc.get(j) + 1)) {
                        requestedDocs.add(nextDocIds.get(j));
                    }
                }
            }
        }

        return requestedDocs;
    }

    // Term-query, AND-query, MIN OR-query
    private Set<Integer> termAndMinor() {
        HashMap<Integer, Integer> docIdCountMap = new HashMap<>();

        String[] queryArray;
        int minIn;

        String tilda_digit = "~\\d+";

        Pattern pattern = Pattern.compile(tilda_digit);
        Matcher matcher = pattern.matcher(query);
        if (matcher.find()) {
            queryArray = query.replaceFirst(tilda_digit, "").split(RegEx.OR);
            minIn = Integer.parseInt(matcher.group(0).replaceFirst("~", ""));
        } else if (query.matches(RegEx.AND)) {
            queryArray = query.split(RegEx.AND);
            minIn = queryArray.length;
        } else {
            queryArray = query.split(RegEx.SPACE);
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
    }

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
        return Arrays.stream(query.split(RegEx.OR))
                .map(word -> invIndex.get(word.trim().toLowerCase()))
                .filter(Objects::nonNull)
                .flatMap(termInv -> termInv.getDocIds().stream())
                .collect(Collectors.toSet());
    }

}
