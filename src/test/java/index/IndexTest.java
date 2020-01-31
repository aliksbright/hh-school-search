package index;

import org.junit.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class IndexTest {
    private static Path indx;
    private static Index index;

    public IndexTest() {

    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        indx = Paths.get("src/test/indx");
        Files.createFile(indx);

        BufferedWriter writer = new BufferedWriter(new FileWriter(indx.toString()));
        writer.write("1 java developer\n" +
                "2 менеджер по продажам\n" +
                "3 город москва продавец консультант\n" +
                "4 ведущий разработчик golang\n" +
                "5 java junior developer\n" +
                "key работа в москве полная занятость зп от 70000\n" +
                "ключ значение\n" +
                "novalue novalue\n");

        index = new Index(indx);
        Map<String, String> map = new HashMap<>();
        map.put("1", "java developer");
        map.put("2", "менеджер по продажам");
        map.put("3", "город москва продавец консультант");
        map.put("4", "ведущий разработчик golang");
        map.put("5", "java junior developer");
        map.put("key", "работа в москве полная занятость зп от 70000");
        map.put("novalue", "novalue");
        map.put("ключ", "значение");
        index.setIndexMap(map);
        writer.close();
    }

    @Test
    public void writeToIndex() throws IOException {
        Map<String, String> lines = new HashMap<>();
        lines.put("2", "менеджер по продажам город самара");
        lines.put("6", "random");
        lines.put("7", "another random");

        index.writeToIndex(lines);
        Set<String> expected = Files.lines(indx).collect(Collectors.toSet());

        Set<String> actual = new HashSet<>();
        actual.add("1 java developer");
        actual.add("2 менеджер по продажам город самара");
        actual.add("3 город москва продавец консультант");
        actual.add("4 ведущий разработчик golang");
        actual.add("5 java junior developer");
        actual.add("key работа в москве полная занятость зп от 70000");
        actual.add("ключ значение");
        actual.add("novalue novalue");
        actual.add("6 random");
        actual.add("7 another random");

        Assert.assertEquals(expected, actual);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        Files.delete(indx);
    }
}