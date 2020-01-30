import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Search {

    private HashMap<String, Map<Integer, List<Integer>>> index;

    public Search(String pathToIndex) throws IOException {
        Index indexFile = new Index(pathToIndex);
        this.index = indexFile.readIndex();
    }

    public Set<Integer> simpleSearch(String query) {
        // Функция для поиска слова в индексе
        if (index.keySet().contains(query.toLowerCase())) {
            return index.get(query.toLowerCase()).keySet();
        }
        return Collections.emptySet();
    }

    private int findNumberOfDocs(HashMap<String, Map<Integer, List<Integer>>> index) {
        // Находим количество документов из индекса
        return index.entrySet().stream()
                .map(x -> x.getValue())
                .map(x -> x.keySet())
                .flatMap(x -> x.stream())
                .max(Integer::compareTo)
                .orElse(0);
    }

    private Set<Integer> getListOfAllDocs(int N) {
        // Возвращаем последовательный список индексоа до максимального кол-ва документов
        // Например N=5, вернем [0, 1, 2, 3, 4]
        if (N == 0) {
            return Set.of(0);
        } else {
            Set<Integer> allDocs = new HashSet<>();
            for (int i = 0; i <= N; ++i) {
                allDocs.add(i);
            }
            return allDocs;
        }
    }

    private Set<Integer> invert(Set<Integer> docs) {
        // Функция используется при логическом NOT
        int maxDoc = findNumberOfDocs(index);
        Set<Integer> allDocs = getListOfAllDocs(maxDoc);
        return allDocs.stream()
                .filter(val -> !docs.contains(val))
                .collect(Collectors.toSet());
    }

    public Set<Integer> boolSearch(String query) {
        // Функция для поиска AND и NOT
        Set<String> terms = Arrays.stream(query.split(" "))
                .filter(term -> !term.equals("and"))
                .filter(term -> !term.equals("not"))
                .collect(Collectors.toSet());
        Map<String, Set<Integer>> dict = terms.stream()
                .collect(Collectors.toMap(term -> term, term -> simpleSearch(term)));

        int maxDoc = findNumberOfDocs(index);
        Set<Integer> result = getListOfAllDocs(maxDoc);
        int sign = 1;

        for (String term: query.split(" ")) {
            if (term.equals("and")) {
                continue;
            }
            else if (term.equals("not")) {
                sign = -1;
                continue;
            }
            else {
                if (sign == 1) {
                    result = result.stream()
                            .filter(doc -> dict.get(term).contains(doc))
                            .collect(Collectors.toSet());
                }
                else {
                    result = result.stream()
                            .filter(doc -> invert(dict.get(term)).contains(doc))
                            .collect(Collectors.toSet());
                    sign = 1;
                }
            }
        }
        return result;
    }

    private Set<Integer> getDocsWithNTerms(String[] terms, int N) {
        // Возвращает номера доков из индекса, где содержится не менее N термов из terms
        HashMap<Integer, Integer> allDocsWithTermsFromQuery = new HashMap<>();
        for (String term : terms) {
            if (!index.containsKey(term)) continue;
            else {
                for (Integer doc : index.get(term).keySet()) {
                    if (!allDocsWithTermsFromQuery.containsKey(doc)) allDocsWithTermsFromQuery.put(doc, 1);
                    else allDocsWithTermsFromQuery.put(doc, allDocsWithTermsFromQuery.get(doc) + 1);
                }
            }
        }
        return allDocsWithTermsFromQuery.entrySet().stream()
                .filter(x -> x.getValue() >= N)
                .map(x -> x.getKey())
                .collect(Collectors.toSet());
    }

    public Set<Integer> phraseSearch(String query) {
        // Функция для поиска фразового запроса
        String[] terms = query.split(" ");
        Set<Integer> commonDocs = getDocsWithNTerms(terms, terms.length);

        Set<Integer> docsWithPhrase = new HashSet<>();

        for (Integer doc : commonDocs) {
            String firstTerm = terms[0];
            for (int pos : index.get(firstTerm).get(doc)) {
                boolean addDoc = false;
                for (int i = 1; i < terms.length; i++) {
                    if (!index.get(terms[i]).get(doc).contains(pos + i)&!index.get(terms[i]).get(doc).contains(pos - i)) break;
                    if (i == terms.length - 1) addDoc = true;
                }
                if (addDoc) {
                    docsWithPhrase.add(doc);
                    break;
                }
            }
        }
        return docsWithPhrase;
    }

    public Set<Integer> orSearch(String query, int N) {
        // Функция для поиска термов, соединенных OR, где кол-во термов не меньше N
        String[] terms = query.split(" ");
        terms = Arrays.stream(terms).filter(x -> !x.equals("or")).toArray(term -> new String[term]);
        return getDocsWithNTerms(terms, N);
    }
}
