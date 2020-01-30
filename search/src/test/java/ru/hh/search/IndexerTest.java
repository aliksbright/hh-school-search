package ru.hh.search;

import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.StringJoiner;

import static org.junit.Assert.*;

public class IndexerTest {

    StringJoiner fileOne = new StringJoiner(System.lineSeparator());
    StringJoiner fileTwo = new StringJoiner(System.lineSeparator());
    Path dir = FileSystems.getDefault().getPath(System.getProperty("java.io.tmpdir"), "testIndexer");
    Path one = FileSystems.getDefault().getPath(System.getProperty("java.io.tmpdir"), "testIndexer", "one.txt");
    Path two = FileSystems.getDefault().getPath(System.getProperty("java.io.tmpdir"), "testIndexer", "two.txt");

    @Before
    public void fill() {
        fileOne.add("java разработчик в IT компанию")
                .add("c++ developer для разработки")
                .add("kotlin при java знаниях");
        fileTwo.add("java разработчик в IT компанию ДВА")
                .add("c++ developer для разработки ДВА")
                .add("kotlin при java знаниях ДВА");
        if (!dir.toFile().exists()) {
            dir.toFile().mkdirs();
        }
        one.toFile().deleteOnExit();
        two.toFile().deleteOnExit();
        try (FileWriter writerOne = new FileWriter(one.toFile());
             FileWriter writerTwo = new FileWriter(two.toFile())
        ) {
            writerOne.write(fileOne.toString());
            writerTwo.write(fileTwo.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void whenSomeFilesToIndex() throws FileNotFoundException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<documents>\n" +
                "    <document documentId=\"1\" lineId=\"1\">java/разработчик/it/компанию/два</document>\n" +
                "    <document documentId=\"2\" lineId=\"2\">c++/developer/разработки/два</document>\n" +
                "    <document documentId=\"3\" lineId=\"3\">kotlin/java/знаниях/два</document>\n" +
                "    <document documentId=\"4\" lineId=\"4\">java/разработчик/it/компанию</document>\n" +
                "    <document documentId=\"5\" lineId=\"5\">c++/developer/разработки</document>\n" +
                "    <document documentId=\"6\" lineId=\"6\">kotlin/java/знаниях</document>\n" +
                "</documents>\n";
        Indexer indexer = new Indexer(
                FileSystems.getDefault().getPath(System.getProperty("java.io.tmpdir"), "/testIndexer"),
                new PrintWriter(out));
        indexer.execute();
        assertEquals(expected, out.toString());
    }

    @Test
    public void whenOneFileToIndex() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<documents>\n" +
                "    <document documentId=\"1\" lineId=\"1\">java/разработчик/it/компанию</document>\n" +
                "    <document documentId=\"2\" lineId=\"2\">c++/developer/разработки</document>\n" +
                "    <document documentId=\"3\" lineId=\"3\">kotlin/java/знаниях</document>\n" +
                "</documents>\n";
        Indexer indexer = new Indexer(
                FileSystems.getDefault().getPath(System.getProperty("java.io.tmpdir"), "/testIndexer/one.txt"),
                new PrintWriter(out));
        indexer.execute();
        assertEquals(expected, out.toString());
    }

}