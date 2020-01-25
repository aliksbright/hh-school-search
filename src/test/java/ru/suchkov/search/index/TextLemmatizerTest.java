package ru.suchkov.search.index;


import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TextLemmatizerTest {

    private TextLemmatizer lemmatizer;

    @Before
    public void init() {
        lemmatizer = new TextLemmatizer();
    }

    @Test
    public void lemmatize() {
        String inputWord = "слово";
        String actual = "слово";
        assertEquals(actual, lemmatizer.lemmatize(inputWord));
    }
}
