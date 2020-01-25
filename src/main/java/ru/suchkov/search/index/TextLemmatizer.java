package ru.suchkov.search.index;

import java.util.HashMap;
import java.util.Map;

public class TextLemmatizer {

    /**
     * По идее, лемматизация (привод слова к первоначальной форме) достаточно тяжеловесный процесс.
     * Поэтому неплохо было бы кэшировать результат лемаатизации для уже используемых форм слова.
     * Поэтому добавим простенький кэш результата лемматизаций
     */
    private Map<String, String> map;

    public TextLemmatizer() {
        map = new HashMap<>();
    }

    /**
     * Лемматизация слова. Возвращает начальную форму слова с помощью морфологического анализа
     * (пока нет)
     * @param word: слово для лемматизации
     * @return начальная форма слова
     */
    public String lemmatize(String word) {
        if (map.containsKey(word)) {
            return map.get(word);
        }
        /*
        TODO лемматизация слова
        Здесь будет (или нет, если не успею) логика пол лемматизации переданного слова
         */
        String baseForm = word;
        map.put(word, baseForm);
        return baseForm;
    }
}
