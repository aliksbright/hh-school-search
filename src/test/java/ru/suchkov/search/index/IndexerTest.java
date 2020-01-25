package ru.suchkov.search.index;


import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class IndexerTest {

    // TODO base dir tests

    private Indexer indexer;
    private Path folder = Paths.get("src","test","resources", "data");

    @Before
    public void init() {
        indexer = new Indexer(folder, folder.resolve("test"));
        folder.resolve("dictionary").toFile().delete();
        folder.resolve("matrix").toFile().delete();
    }

    @Test
    public void testBuildIndex(){
        indexer.build();

        assertTrue(folder.resolve("dictionary").toFile().exists());
        assertTrue(folder.resolve("matrix").toFile().exists());
    }
}
