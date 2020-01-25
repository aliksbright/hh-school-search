package ru.suchkov.search.index;

import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.*;

public class DictionaryTest {

    private Dictionary dictionary;
    private String TEST_INDEX_DIR = "/Users/denis/Desktop/hh-school-search/src/test/resources/data";

    @Before
    public void init() {
        dictionary = new Dictionary();
        dictionary.add("привет");
        dictionary.add("мир");
        dictionary.add("привет");
        dictionary.add("пока");
        dictionary.add("1");
    }

    @Test
    public void add() {
        int sizeBefore = dictionary.getSize();
        dictionary.add("новое");

        int newSize = dictionary.getSize();
        assertEquals(1, newSize  - sizeBefore);

        dictionary.add("новое");
        assertEquals(newSize, dictionary.getSize());

        assertTrue(dictionary.getDictionary().containsKey("новое"));
    }

    @Test
    public void getKey() {
        assertEquals(1, dictionary.getKey("привет"));
    }

    @Test
    public void saveAndRestore() {
        dictionary.save(Paths.get(TEST_INDEX_DIR));

        Dictionary restored = new Dictionary();
        restored.load(Paths.get(TEST_INDEX_DIR));

        assertEquals(dictionary.getDictionary(), restored.getDictionary());
        assertEquals(dictionary.getSize(), restored.getSize());
    }
}