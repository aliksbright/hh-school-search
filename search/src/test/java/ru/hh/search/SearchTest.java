package ru.hh.search;

import org.junit.Before;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

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
            .add("<document documentId=\"7\" lineId=\"7\">учитель/классрук/химии/школа</document>")
            .add("<document documentId=\"8\" lineId=\"8\">учитель/школа/завуч/химии</document>")
            .add("<document documentId=\"9\" lineId=\"9\">учитель/математики/завуч</document>")
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
        Map<Integer, String> result = search.pSearch(search.search());
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
        assertEquals(new HashMap<Integer, String>(), search.pSearch(search.search()));
    }

    @Test
    public void whenSearchNotInIndexThen() {
        Search search = new Search(this.index, "дворник джаз");
        search.createInvIndex();
        assertEquals(new HashSet<Document>(), search.search());
    }

    @Test
    public void whenAndNotSearch() {
        Search search = new Search(this.index, "учитель and химии");
        search.createInvIndex();
        List<Integer> actual = search.andNotSearch().stream().map(Document::getLineId).collect(Collectors.toList());
        assertEquals(List.of(7, 8), actual);
    }

    @Test
    public void whenAndNotSearchNotExist() {
        Search search = new Search(this.index, "учитель and химии and c++");
        search.createInvIndex();
        search.andNotSearch().forEach(el -> System.out.println(el.getLineId()));
        List<Integer> actual = search.andNotSearch().stream().map(Document::getLineId).collect(Collectors.toList());
        assertEquals(List.of(), actual);
    }

    @Test
    public void whenAndNotSearchWithNot() {
        Search search = new Search(this.index, "учитель and химии not завуч");
        search.createInvIndex();
        List<Integer> actual = search.andNotSearch().stream().map(Document::getLineId).collect(Collectors.toList());
        assertEquals(List.of(7), actual);
    }

    @Test
    public void whenAndNotSearchWithNotAndOtherWords() {
        Search search = new Search(this.index, "учитель and химии java not завуч слесарь гпгп");
        search.createInvIndex();
        List<Integer> actual = search.andNotSearch().stream().map(Document::getLineId).collect(Collectors.toList());
        assertEquals(List.of(7), actual);
    }

    @Test
    public void whenPhraseSearch() {
        Search search = new Search(this.index, "менеджер \"учитель школа завуч химии\" it");
        search.createInvIndex();
        assertEquals(
                List.of(8),
                search.phraseSearch().stream().map(Document::getLineId).collect(Collectors.toList())
        );
    }
}