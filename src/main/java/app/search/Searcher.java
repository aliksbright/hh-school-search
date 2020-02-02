package app.search;

import app.structure.InvertedIndex;
import app.structure.Term;
import app.structure.TermInv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Searcher {

    enum Mode {
        TERM, AND, NOT, OR, MINOR, PHRASE
    }

    private String query;
    private Mode mode;
    private HashMap<String, TermInv> invIndex;

    public Searcher(String query, HashMap<String, TermInv> invIndex) {
        this.query = query
                .replaceAll("\\s\\s", " ")
                .replaceAll("[.,/!?;:]", "");
        this.invIndex = invIndex;
    }

    public Set<Integer> getRequestedDocs() {
        List<Integer> requestedDocs = null;
        if (query.contains("\""))
            requestedDocs = phrase();
        else if (query.contains("~"))
            requestedDocs = minor();
        else if (query.contains("OR"))
            requestedDocs = or();
        else if (query.contains("NOT"))
            requestedDocs = not();
        else if (query.contains("AND") || query.contains("\\s"))
            requestedDocs = and();
        else
            requestedDocs = term();
        return Set.copyOf(requestedDocs);
    }

    private List<Integer> phrase() {
        List<String> queryList = List.of(query.replaceAll("\"", "").split("\\s"));
        return null;
    }

    private List<Integer> and() {
        return null;
    }

    private List<Integer> or() {
        return null;
    }

    private List<Integer> minor() {
        return null;
    }

    private List<Integer> not() {
        return null;
    }

    private List<Integer> term() {
        TermInv termInv = invIndex.get(query.split(" ")[0]);
        if (termInv == null) {
            return List.of(-1);
        } else {
            return termInv.getDocIds();
        }
    }

}
