package ru.suchkov.search.index;

import org.junit.Test;

import static org.junit.Assert.*;

public class TextNormalizerTest {

    @Test
    public void clearPunctuation() {
        String textWithPunctuation = "Привет, друг! Как\\ \"твои дела??? спросили/ его;";
        String output = "Привет друг Как твои дела спросили его";
        assertEquals(output, TextNormalizer.clearPunctuation(textWithPunctuation));
    }

    @Test
    public void toLowerCase() {
        String inputText = "ПрИвЕТ ДРуже";
        String output = "привет друже";
        assertEquals(output, TextNormalizer.toLowerCase(inputText));
    }

    @Test
    public void split() {
        String inputText = "Один ДВА     три       4";
        String[] output = {"Один", "ДВА", "три", "4"};
        assertArrayEquals(output, TextNormalizer.split(inputText));
    }

    @Test
    public void normalize() {
        String inputText = "Один, '' \" ? : ДВА!!     три       4";
        String[] output = {"один", "два", "три", "4"};
        assertArrayEquals(output, TextNormalizer.normalize(inputText));
    }
}
