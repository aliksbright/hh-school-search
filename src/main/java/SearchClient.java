import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SearchClient {

    private Index index;

    public SearchClient(Index index) {
        this.index = index;
    }

    public Set<Integer> findDocuments(String query) {

        List<String> subQueries = Arrays.asList(query.replaceAll("\\(|\\)|,|\\.|;", " ").split("\\s+OR\\s+"));
        Set<Integer> documentIds = new HashSet<>();

        subQueries.stream()
                .map(this::findSubQuery)
                .forEach(documentIds::addAll);
        return documentIds;
    }

    private Set<Integer> findSubQuery(String query) {
        String[] allTerms = query.split("\\s+AND\\s+");

        Set<String> positiveTerms = new HashSet<>();
        Set<String> negativeTerms = new HashSet<>();

        for (String term : allTerms) {
            List<String> terms = Arrays.asList(term.split("\\s+"));
            if (terms.size() == 2 && terms.get(0).equals("NOT")) {
                negativeTerms.add(terms.get(1).toLowerCase());
            } else if (terms.size() == 1){
                positiveTerms.add(term.toLowerCase());
            } else {
                System.out.println("Некорректная часть запроса - " + term);
            }
        }

        Set<Integer> documentIds;
        if (positiveTerms.size() > 0) {
            documentIds = index.getTermDocumentMap().getOrDefault(positiveTerms.iterator().next(), Collections.emptyList())
                    .stream()
                    .map(IndexedDocument::getDocumentId)
                    .collect(Collectors.toSet());
            for (String term : positiveTerms) {
                documentIds.retainAll(index.getTermDocumentMap().getOrDefault(term, Collections.emptyList()).stream()
                        .map(IndexedDocument::getDocumentId)
                        .collect(Collectors.toSet()));
            }
        } else {
            documentIds = index.getTermDocumentMap().values().stream()
                    .flatMap(List::stream)
                    .map(IndexedDocument::getDocumentId)
                    .collect(Collectors.toSet());
        }

        for (String negativeTerm : negativeTerms) {
            index.getTermDocumentMap().getOrDefault(negativeTerm, Collections.emptyList()).stream()
                    .map(IndexedDocument::getDocumentId)
                    .forEach(documentIds::remove);
        }
        return documentIds;
    }
}
