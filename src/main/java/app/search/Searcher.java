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
        setMode(query);
    }

    private void setMode(String query) {
        if (query.contains("\""))
            this.mode = Mode.PHRASE;
        else if (query.contains("~"))
            this.mode = Mode.MINOR;
        else if (query.contains("OR"))
            this.mode = Mode.OR;
        else if (query.contains("NOT"))
            this.mode = Mode.NOT;
        else if (query.contains("AND") || query.contains("\\s"))
            this.mode = Mode.AND;
        else
            this.mode = Mode.TERM;
    }

    private List<Integer> phrase() {
        List<String> queryList = List.of(query.replaceAll("\"", "").split("\\s"));
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

    public Set<Integer> getRequestedDocs() {
        switch (mode) {
            case TERM:
                return Set.copyOf(term());
            default:
                return null;
        }
    }
}
