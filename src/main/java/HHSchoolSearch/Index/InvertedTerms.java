package HHSchoolSearch.Index;

import HHSchoolSearch.Model.Document;
import HHSchoolSearch.Model.Term;
import HHSchoolSearch.Model.Token;
import com.google.gson.annotations.Expose;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class InvertedTerms
{
    @Expose
    private HashMap<Integer, Set<Token>> entries;

    private static InvertedTerms singleton;
    private InvertedTerms() { }

    public static boolean initialize(InvertedTerms providedSingleton) throws Exception
    {
        if (providedSingleton != null)
        {
            if (providedSingleton.entries == null)
                throw new Exception("Invalid JSON content in res/index/inverted_terms.json");

            singleton = providedSingleton;
        }
        else
        {
            singleton = new InvertedTerms();
            setEntries(new HashMap<>());
        }

        return true;
    }

    public static HashMap<Integer, Set<Token>> getEntries() { return singleton.entries; }
    public static void setEntries(HashMap<Integer, Set<Token>> entries) { singleton.entries = entries; }

    public static InvertedTerms getSingleton() { return singleton; }

    public static void registerEntriesInDoc(Document doc)
    {
        for (var token : doc.getTokens()) registerEntry(token);
    }

    public static void registerEntry(Token token)
    {
        var term = AllTerms.findTerm(token.getTermId());
        var termId = term.getTermId();

        Set<Token> tokens;
        if (getEntries().containsKey(termId))
            tokens = getEntries().get(termId);
        else
        {
            tokens = new HashSet<>();
            getEntries().put(termId, tokens);
        }

        tokens.add(token);
    }

    public static boolean containsTerm(Term term)
    {
        return getEntries().containsKey(term.getTermId());
    }

    public static Set<Token> getTermEntries(Term term)
    {
        if (term == null) return new HashSet<>();

        var termId = term.getTermId();

        if (getEntries().containsKey(termId))
            return getEntries().get(termId);
        return new HashSet<>();
    }
}
