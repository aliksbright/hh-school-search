package index;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class NormalizatorTest {

    public NormalizatorTest() {

    }

    @Test
    public void normalizeStrings() throws IOException {
        Path orig = Paths.get("src/test/orig");
        Files.createFile(orig);

        BufferedWriter writer = new BufferedWriter(new FileWriter(orig.toString()));
        writer.write("1 JAVA developer     \n" +
                "      2 менеджер по продажам\n" +
                "3 город Москва, продавец консультант\n" +
                "4 ведущий разработчик : Golang\n" +
                "5 java            junior             developer\n" +
                "key работа в москве, полная занятость з/п от 70000\n" +
                "ключ значение\n" +
                "novalue\n");
        writer.close();

        Map<String, String> expected = Normalizator.normalizeStrings(orig);
        Files.delete(orig);

        Map<String, String> actual = new HashMap<>();
        actual.put("1", "java developer");
        actual.put("2", "менеджер по продажам");
        actual.put("3", "город москва продавец консультант");
        actual.put("4", "ведущий разработчик golang");
        actual.put("5", "java junior developer");
        actual.put("key", "работа в москве полная занятость зп от 70000");
        actual.put("ключ", "значение");
        actual.put("novalue", "novalue");

        Assert.assertEquals(expected, actual);
    }
}