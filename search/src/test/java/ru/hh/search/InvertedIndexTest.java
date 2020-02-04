package ru.hh.search;

import org.junit.Before;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;
import java.util.StringJoiner;

import static org.junit.Assert.*;

public class InvertedIndexTest {

    private Path dir = FileSystems.getDefault().getPath(System.getProperty("java.io.tmpdir"), "testInverted");
    private Path index = FileSystems.getDefault().getPath(dir.toString(), "index.xml");

    private StringJoiner strIndex = new StringJoiner(System.lineSeparator())
            .add("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>")
            .add("<documents>")
            .add("<document documentId=\"1\" lineId=\"1\">java/разработчик/it/компанию</document>")
            .add("<document documentId=\"2\" lineId=\"2\">manager/продаж/it/компанию</document>")
            .add("<document documentId=\"3\" lineId=\"3\">менеджер/продаж</document>")
            .add("<document documentId=\"4\" lineId=\"4\">слесарь/ремонтник</document>")
            .add("<document documentId=\"5\" lineId=\"5\">администратор/конгресс/центр</document>")
            .add("<document documentId=\"6\" lineId=\"6\">технический/специалист/конгресс/центр</document>")
            .add("</documents>");

    @Before
    public void fill() throws IOException {
        if (!dir.toFile().exists()) {
            dir.toFile().mkdirs();
        }
        try (FileWriter writer = new FileWriter(index.toFile())) {
            writer.write(strIndex.toString());
        }
        index.toFile().deleteOnExit();
        dir.toFile().deleteOnExit();
    }

    @Test
    public void whenCreateInvIndexThenCountOfWordIs13() {
        InvertedIndex invertedIndex = new InvertedIndex(index);
        assertEquals(14, invertedIndex.createInvIndex().size());
    }

    @Test
    public void whenCreateInvIndexThen() {
        InvertedIndex invertedIndex = new InvertedIndex(index);
        List<Document> docs = invertedIndex.createInvIndex().get("конгресс");
        assertEquals("администратор", docs.get(0).getTerms().get(0).getValue());
        assertEquals("конгресс", docs.get(0).getTerms().get(1).getValue());
        assertEquals("центр", docs.get(0).getTerms().get(2).getValue());
    }

    @Test
    public void whenCreateInvIndexThenTermPositionIsRight() {
        InvertedIndex invertedIndex = new InvertedIndex(index);
        List<Document> docs = invertedIndex.createInvIndex().get("конгресс");
        List<Term> terms = docs.get(1).getTerms();
        assertEquals("технический", terms.get(0).getValue());
        assertEquals(0, terms.get(0).getPosition());
        assertEquals("специалист", terms.get(1).getValue());
        assertEquals(1, terms.get(1).getPosition());
        assertEquals("конгресс", terms.get(2).getValue());
        assertEquals(2, terms.get(2).getPosition());
        assertEquals("центр", terms.get(3).getValue());
        assertEquals(3, terms.get(3).getPosition());
    }

}