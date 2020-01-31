package app.search;

import app.structure.TermInv;

import java.util.HashMap;

public class Searcher {

    enum Mode {
        TERM, AND, NOT, OR, MINOR, PHRASE
    }

    private String query;
    private Mode mode;
    private HashMap<String, TermInv> invIndex;

    public Searcher(String query, HashMap<String, TermInv> invIndex) {
        this.query = query;
        this.invIndex = invIndex;
        setMode(query);
    }

    public void setMode(String query) {
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
}
