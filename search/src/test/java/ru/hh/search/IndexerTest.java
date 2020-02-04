package ru.hh.search;

import org.junit.Before;
import org.junit.Test;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
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
    public void whenSomeFilesToIndex() throws IOException, ParserConfigurationException, TransformerException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        StringJoiner expected = new StringJoiner(System.lineSeparator())
                .add("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>")
                .add("<documents>")
                .add("    <document documentId=\"1\" lineId=\"1\">java/разработчик/it/компанию/два</document>")
                .add("    <document documentId=\"2\" lineId=\"2\">c++/developer/разработки/два</document>")
                .add("    <document documentId=\"3\" lineId=\"3\">kotlin/java/знаниях/два</document>")
                .add("    <document documentId=\"4\" lineId=\"4\">java/разработчик/it/компанию</document>")
                .add("    <document documentId=\"5\" lineId=\"5\">c++/developer/разработки</document>")
                .add("    <document documentId=\"6\" lineId=\"6\">kotlin/java/знаниях</document>")
                .add("</documents>")
                .add("");
        Indexer indexer = new Indexer(new PrintWriter(out));
        indexer.execute(FileSystems.getDefault().getPath(System.getProperty("java.io.tmpdir"), "testIndexer"));
        assertEquals(expected.toString(), out.toString());
    }

    @Test
    public void whenOneFileToIndex() throws IOException, TransformerException, ParserConfigurationException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        StringJoiner expected = new StringJoiner(System.lineSeparator())
                .add("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>")
                .add("<documents>")
                .add("    <document documentId=\"1\" lineId=\"1\">java/разработчик/it/компанию</document>")
                .add("    <document documentId=\"2\" lineId=\"2\">c++/developer/разработки</document>")
                .add("    <document documentId=\"3\" lineId=\"3\">kotlin/java/знаниях</document>")
                .add("</documents>")
                .add("");
        Indexer indexer = new Indexer(new PrintWriter(out));
        indexer.execute(FileSystems.getDefault().getPath(System.getProperty("java.io.tmpdir"), "testIndexer", "one.txt"));
        assertEquals(expected.toString(), out.toString());
    }

}