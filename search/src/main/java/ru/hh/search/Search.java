package ru.hh.search;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static ru.hh.search.Util.getTokens;

/**
 * Поиск по инвертированному индексу.
 * @author Andrey Bukhtoyarov (andreymedoed@gmail.com).
 * @version %Id%.
 * @since 0.1.
 */
public class Search {
    /**
     * Путь к индексу на диске.
     */
    private Path index;
    /**
     * Поисковый запрос.
     */
    private String query;
    /**
     * Инвертированный индекс.
     */
    private HashMap<String, ArrayList<Document>> invertedIndex;

    public Search(Path index, String query) {
        this.index = index;
        this.query = query;
    }

    public boolean createInvIndex() {
        this.invertedIndex = new InvertedIndex(this.index).createInvIndex();
        return this.invertedIndex.size() > 0;
    }

    /**
     * Простой поиск по словам
     * @return множество найденных документов.
     */
    public Set<Document> search() {
        List<String> tokens = getTokens(this.query);
        return tokens.stream().map(term -> this.invertedIndex.getOrDefault(term, new ArrayList<>()))
                .flatMap(Collection::stream).collect(Collectors.toSet());
    }

    /**
     * Используется тот же search(),
     * но вывод получается такой: id - строка из индекса, с подсвеченными найденными словами.
     * @return словарь.
     */
    public Map<Integer, String> pSearch() {
        return search().stream().collect(Collectors.toMap(Document::getLineId, this::pString));
    }

    /**
     * Подсвечивает найденные слова.
     * @param document документ
     * @return строка с подсвеченными словами.
     */
    private String pString(Document document) {
        Set<String> terms = new HashSet<>(getTokens(this.query));
        return document.getTermsString().stream()
                .map(word -> terms.contains(word) ? word.toUpperCase() : word)
                .collect(Collectors.joining(" "));
    }

}
