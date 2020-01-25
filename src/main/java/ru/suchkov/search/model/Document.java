package ru.suchkov.search.model;

import lombok.Data;

/**
 * Класс документа.
 */
@Data
public class Document {

    /**
     * Некоторое число в документе для реализации поиска по диапазону значений (если успею)
     */
    private int number;

    /**
     * Информативная часть документа, по этому полю происходит полнотекстовый поиск
     */
    private String text;
}
