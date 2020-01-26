package ru.suchkov.search.search;

import ru.suchkov.search.index.Dictionary;
import ru.suchkov.search.index.Matrix;
import ru.suchkov.search.index.TextLemmatizer;
import ru.suchkov.search.index.TextNormalizer;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import static java.lang.String.format;

public class Searcher {

    private Matrix matrix;
    private Dictionary wordDictionary;
    private TextLemmatizer textLemmatizer;
    private Path indexDirectory;
    private Path indexedFile;

    public Searcher(Path indexDirectory, Path indexedFile) {
        this.indexDirectory = indexDirectory;
        this.indexedFile = indexedFile;
        matrix = new Matrix();
        matrix.load(indexDirectory);
        wordDictionary = new Dictionary();
        wordDictionary.load(indexDirectory);
        textLemmatizer = new TextLemmatizer();
    }

    /**
     * Возвращает спиосок id документов, содержащих искоме слово
     * @param word: искомое слово
     * @return набор id документов, содержащих искомое слово
     */
    public Set<Integer> searchText(String word) {
        return matrix.getMatrix().getOrDefault(getWordId(word), new HashSet<>());
    }

    /**
     * Возвращает список id документов, удовлетворяющих запросу.
     * В настоящий момент реализовано:
     * - поиск одного слова
     * - поиск исключения одного слова
     * - поиск двух слов с операцие "и"
     * - поиск двух слов с операцией "или"
     * @param expression: поисковый запрос
     * @return набор id документов, удовлетворяющих искомому запросу
     * (пустое множество в случае некорректного запроса)
     */
    public Set<Integer> searchExpression(String expression) {
        String[] tokens = TextNormalizer.normalize(expression);
        if (tokens.length == 1) {
            return searchText(tokens[0]);
        }
        if (tokens.length == 2 && "not".equals(tokens[0])) {
            int wordId = getWordId(tokens[1]);
            int docsWithWord = matrix.extractLine(wordId);

            int invertDocs = (1 << matrix.getDocsQuantity()) - 1 - docsWithWord;
            return getDocuments(invertDocs, matrix.getDocsQuantity());
        }
        if (tokens.length == 3) {
            int wordId1 = getWordId(tokens[0]);
            int wordId2 = getWordId(tokens[2]);
            int docsWithWord1 = matrix.extractLine(wordId1);
            int docsWithWord2 = matrix.extractLine(wordId2);

            int result = 0;

            if ("and".equals(tokens[1])){
                result = docsWithWord1 & docsWithWord2;
            }
            if ("or".equals(tokens[1])) {
                result = docsWithWord1 | docsWithWord2;
            }

            return getDocuments(result, matrix.getDocsQuantity());
        }
        return new HashSet<>();
    }

    /**
     * Получение id слова из словаря для ненормализованного и нелематизированного слова
     * @param word: искомое слово (в любой форме)
     * @return индекс слова в словаре
     */
    private int getWordId(String word) {
        return wordDictionary.getKey(TextNormalizer.toLowerCase(textLemmatizer.lemmatize(word)));
    }

    /**
     * Получение списка документов по двоичной маске соответсвия матрицы инцедентности
     * @param lineExtraction: число, двоичное представление которого отражает двоичную маску
     * @param size: количество документов в индексе
     * @return список id документов
     */
    private Set<Integer> getDocuments(int lineExtraction, int size) {
        Set<Integer> docs = new HashSet<>();
        String line = getBinaryLine(lineExtraction, size);
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '1') {
                docs.add(i + 1);
            }
        }
        return docs;
    }

    /**
     * Приведение числа двоичной маски к строке нужной длины, состоящей из 0 и 1
     * @param lineExtraction: число, двоичное представление которого отражает двоичную маску
     * @param size: количество документов в индексе
     * @return строку нужной длины, состоящей из 0 и 1
     */
    private String getBinaryLine(int lineExtraction, int size) {
        return format("%" + size + "s", Integer.toBinaryString(lineExtraction)).replace(" ", "0");
    }
}
