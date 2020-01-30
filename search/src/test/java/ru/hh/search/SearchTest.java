package ru.hh.search;

import org.junit.Before;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.*;

import static org.junit.Assert.*;

public class SearchTest {

    private Path dir = FileSystems.getDefault().getPath(System.getProperty("java.io.tmpdir"), "testSearch");
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
    public void whenSearch() {
        Search search = new Search(this.index, "java java конгресс");
        search.createInvIndex();
        Set<Document> result = search.search();
        Set<Document> expected = new HashSet<>(
                Set.of(
                        new Document().setLineId(5)
                                .addTerm(new Term(0, "администратор"))
                                .addTerm(new Term(1, "конгресс"))
                                .addTerm(new Term(2, "центр")),
                        new Document().setLineId(1)
                                .addTerm(new Term(0, "java"))
                                .addTerm(new Term(1, "разработчик"))
                                .addTerm(new Term(2, "it"))
                                .addTerm(new Term(3, "компанию")),
                        new Document().setLineId(6)
                                .addTerm(new Term(0, "технический"))
                                .addTerm(new Term(1, "специалист"))
                                .addTerm(new Term(2, "конгресс"))
                                .addTerm(new Term(3, "центр"))
                )
        );
        assertEquals(expected, result);
    }

    @Test
    public void whenPSearch() {
        Search search = new Search(this.index, "java it java ремонтник");
        search.createInvIndex();
        Map<Integer, String> result = search.pSearch();
        Map<Integer, String> expected = new HashMap<>(
                Map.of(1, "JAVA разработчик IT компанию",
                        2, "manager продаж IT компанию",
                        4, "слесарь РЕМОНТНИК")
        );
        assertEquals(expected, result);
    }

    @Test
    public void whenQueryNotExistThen() {
        Search search = new Search(this.index, "");
        search.createInvIndex();
        assertEquals(new HashMap<Integer, String>(), search.pSearch());
    }

    @Test
    public void whenSearchNotInIndexThen() {
        Search search = new Search(this.index, "дворник джаз");
        search.createInvIndex();
        assertEquals(new HashSet<Document>(), search.search());
    }
}