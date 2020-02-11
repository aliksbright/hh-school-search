package HHSchoolSearch.Index;

import HHSchoolSearch.Model.Document;
import HHSchoolSearch.Model.Term;
import HHSchoolSearch.Model.Token;
import com.google.gson.annotations.Expose;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class InvertedDocs
{
    @Expose
    private HashMap<Integer, Set<Integer>> entries;

    private static InvertedDocs singleton;
    private InvertedDocs() { }

    public static boolean initialize(InvertedDocs providedSingleton) throws Exception
    {
        if (providedSingleton != null)
        {
            if (providedSingleton.entries == null)
                throw new Exception("Invalid JSON content in res/index/inverted_docs.json");

            singleton = providedSingleton;
        }
        else
        {
            singleton = new InvertedDocs();
            setEntries(new HashMap<>());
        }

        return true;
    }

    public static HashMap<Integer, Set<Integer>> getEntries() { return singleton.entries; }
    public static void setEntries(HashMap<Integer, Set<Integer>> entries) { singleton.entries = entries; }

    public static InvertedDocs getSingleton() { return singleton; }

    public static void registerEntriesInDoc(Document doc)
    {
        for (var token : doc.getTokens()) registerEntry(token);
    }

    public static void registerEntry(Token token)
    {
        var term = AllTerms.findTerm(token.getTermId());
        var termId = term.getTermId();

        Set<Integer> docs;
        if (getEntries().containsKey(termId))
            docs = getEntries().get(termId);
        else
        {
            docs = new HashSet<>();
            getEntries().put(termId, docs);
        }

        docs.add(token.getDocumentId());
    }

    public static boolean containsTerm(Term term)
    {
        return getEntries().containsKey(term.getTermId());
    }

    public static Set<Integer> getTermEntries(Term term)
    {
        if (term == null) return new HashSet<>();

        var termId = term.getTermId();

        if (getEntries().containsKey(termId))
            return getEntries().get(termId);
        return new HashSet<>();
    }
}
