package ru.suchkov.search.index;

/**
 * Утильный класс, для нормализации текста
 * (удаление спецсимволов, разбиение на токены, etc.)
 */
public class TextNormalizer {

    private TextNormalizer() {
    }

    /**
     * Удаляет все знаки препинания из строки
     * @param text: входная строка
     * @return строка без знаков припинания
     */
    public static String clearPunctuation(String text) {
        return text.replaceAll("\\p{Punct}", "");
    }

    /**
     * Приводит строку к нижному регистру
     * @param text: входная строка
     * @return строка приведенная к нижнему регистру
     */
    public static String toLowerCase(String text) {
        return text.toLowerCase();
    }

    /**
     * Разбивает строку на токены
     * @param text: входная строка
     * @return массив токенов, разделенных по пробелам
     */
    public static String[] split(String text) {
        return text.split("\\s+");
    }

    /**
     * Полностью нормализует строку, разбивая ее на токены
     * @param text: входная строка
     * @return массив токенов, разделенных по пробелам
     */
    public static String[] normalize(String text) {
        return split(toLowerCase(clearPunctuation(toLowerCase(text))));
    }
}
