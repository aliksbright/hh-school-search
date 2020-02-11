package HHSchoolSearch.Index;

import HHSchoolSearch.Model.Term;
import com.google.gson.annotations.Expose;

import java.util.HashMap;

public class AllTerms
{
    @Expose
    private int lastIssuedId;
    @Expose
    private HashMap<Integer, Term> termsIdMap;
    @Expose
    private HashMap<String, Term> termsStrMap;

    private static AllTerms singleton;
    public static AllTerms getSingleton() { return singleton; }

    private static int getLastIssuedId() { return singleton.lastIssuedId; }
    private static void setLastIssuedId(int lastIssuedId) { singleton.lastIssuedId = lastIssuedId; }

    public static HashMap<Integer, Term> getTermsIdMap() { return singleton.termsIdMap; }
    public static void setTermsIdMap(HashMap<Integer, Term> termsIdMap) { singleton.termsIdMap = termsIdMap; }

    public static HashMap<String, Term> getTermsStrMap() { return singleton.termsStrMap; }
    public static void setTermsStrMap(HashMap<String, Term> termsStrMap) { singleton.termsStrMap = termsStrMap; }

    private AllTerms() { }

    public static boolean initialize(AllTerms providedSingleton) throws Exception
    {
        if (providedSingleton != null)
        {
            if (providedSingleton.termsIdMap == null || providedSingleton.termsStrMap == null)
                throw new Exception("Invalid JSON content in res/index/all_terms.json");

            singleton = providedSingleton;
        }
        else
        {
            singleton = new AllTerms();

            setLastIssuedId(-1);
            setTermsIdMap(new HashMap<>());
            setTermsStrMap(new HashMap<>());
        }

        return true;
    }

    public static void registerTerm(Term term)
    {
        getTermsIdMap().put(term.getTermId(), term);
        getTermsStrMap().put(term.getTermString(), term);
    }

    public static boolean containsTerm(String termString)
    {
        return getTermsStrMap().containsKey(termString);
    }
    public static Term findTerm(String termString)
    {
        return getTermsStrMap().getOrDefault(termString, null);
    }

    public static boolean containsTerm(int termId)
    {
        return getTermsIdMap().containsKey(termId);
    }
    public static Term findTerm(int termId)
    {
        return getTermsIdMap().getOrDefault(termId, null);
    }

    private static int issueId()
    {
        var newId = getLastIssuedId() + 1;
        setLastIssuedId(newId);

        return newId;
    }

    public static Term provideTerm(String termString)
    {
        if (getTermsStrMap().containsKey(termString))
            return getTermsStrMap().get(termString);
        else
        {
            var termId = issueId();
            var term = new Term(termId, termString);

            registerTerm(term);
            return term;
        }
    }
}
