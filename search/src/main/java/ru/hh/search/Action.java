package ru.hh.search;

/**
 * Интерфейс для запуска индексатора или поисковика.
 * @author Andrey Bukhtoyarov (andreymedoed@gmail.com).
 * @version %Id%.
 * @since 0.1.
 */
public interface Action {
    /**
     * Запускает одну из частей программы.
     */
    void start();
}
