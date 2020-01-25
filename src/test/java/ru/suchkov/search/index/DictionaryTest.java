package ru.suchkov.search.index;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class DictionaryTest {

    private Dictionary dictionary;
    private Path folder = Paths.get("src","test","resources", "data");

    @Before
    public void init() {
        dictionary = new Dictionary();
        dictionary.add("привет");
        dictionary.add("мир");
        dictionary.add("привет");
        dictionary.add("пока");
        dictionary.add("1");

        folder.resolve("dictionary").toFile().delete();
        folder.resolve("matrix").toFile().delete();
    }

    @After
    public void tearDown() throws Exception {
        folder.resolve("dictionary").toFile().delete();
        folder.resolve("matrix").toFile().delete();
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
        dictionary.save(folder);

        Dictionary restored = new Dictionary();
        restored.load(folder);

        assertEquals(dictionary.getDictionary(), restored.getDictionary());
        assertEquals(dictionary.getSize(), restored.getSize());
    }
}