package ru.suchkov.search.search;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.suchkov.search.index.Indexer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class SearcherTest {

    private Searcher searcher;
    private static Path folder = Paths.get("src","test","resources", "data");

    @BeforeClass
    public static void buildIndex() {
        new Indexer(folder, folder.resolve("test")).build();
    }

    @AfterClass
    public static void removeIndex() {
        folder.resolve("dictionary").toFile().delete();
        folder.resolve("matrix").toFile().delete();
    }

    @Before
    public void init() {
        searcher = new Searcher(folder, folder.resolve("test"));
    }

    @Test
    public void searchText() {
        assertEquals(new HashSet<>(asList(1, 2, 3, 6, 7)), searcher.searchText("A"));
    }

    @Test
    public void searchExpressionNot() {
        assertEquals(new HashSet<>(asList(4, 5, 8, 9, 10, 11, 12)), searcher.searchExpression("not A"));
    }

    @Test
    public void searchExpressionAnd() {
        assertEquals(new HashSet<>(asList(1, 3)), searcher.searchExpression("B and A"));
    }

    @Test
    public void searchExpressionOr() {
        assertEquals(new HashSet<>(asList(1, 2, 3, 6, 7, 12)), searcher.searchExpression("B or A"));
    }
}