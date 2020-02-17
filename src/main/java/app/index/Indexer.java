package app.index;

import app.structure.Document;
import app.structure.Term;
import app.util.RegEx;

import java.util.ArrayList;
import java.util.List;

public class Indexer {

    public static List<String> getTerms(Document doc) {
        return List.of(doc.getPrimary().toLowerCase().split(RegEx.PUNCT_WITH_SPACE));
    }

    public static List<String> getLemmatizedTerms(List<String> terms) {
        return null;
    }

    public static List<Term> getTermsAndPositions(List<String> terms) {
        List<Term> termPosition = new ArrayList<>();
        for (int i = 0; i < terms.size(); i++) {
            Term term = new Term(terms.get(i), i);
            termPosition.add(term);
        }
        return termPosition;
    }

    public static List<Term> removeStopWords(List<Term> terms, ArrayList<String> stopWords) {
        for (String word : stopWords) {
            terms.removeIf(term -> term.getValue().equals(word));
        }
        return terms;
    }

}
