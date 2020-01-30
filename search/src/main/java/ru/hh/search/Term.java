package ru.hh.search;

import java.util.Objects;

/**
 * Абстракция терма.
 * @author Andrey Bukhtoyarov (andreymedoed@gmail.com).
 * @version %Id%.
 * @since 0.1.
 */
public class Term {
    /**
     * Позиция терма в документе.
     */
    private int position;
    /**
     * Значение терма.
     */
    private String value;

    public Term(int position, String value) {
        this.position = position;
        this.value = value;
    }

    public int getPosition() {
        return position;
    }

    public String getValue() {
        return value;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Term term = (Term) o;
        return position == term.position &&
                Objects.equals(value, term.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, value);
    }

    @Override
    public String toString() {
        return this.value;
    }
}
