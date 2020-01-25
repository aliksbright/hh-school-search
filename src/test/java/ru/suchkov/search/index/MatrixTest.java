package ru.suchkov.search.index;

import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;

public class MatrixTest {

    private Matrix matrix;
    private String TEST_INDEX_DIR = "/Users/denis/Desktop/hh-school-search/src/test/resources/data";

    @Before
    public void init() {
        matrix = new Matrix();
    }

    @Test
    public void testIntegerParse() {
        int a = Integer.parseInt( "101", 2); // 5
        int b = Integer.parseInt("1010", 2); // 10
        int aOrB = a | b;
        int aAndB = a & b;
        int c = Integer.parseInt("1111", 2); // 15
        int d = Integer.parseInt("0000", 2); // 0
        assertEquals(aOrB, c);
        assertEquals(aAndB, d);
    }

    @Test
    public void activateMatrix() {
        int[] documentIds = {1, 2, 3, 4, 5};
        int[] wordIds = {15, 27, 39, 43, 53, 55};


        Map<Integer, Set<Integer>> map = new HashMap<>();
        map.put(15, new HashSet<>(asList(2, 5)));
        map.put(27, new HashSet<>(singletonList(1)));
        map.put(39, new HashSet<>(asList(1, 3, 4)));
        map.put(43, new HashSet<>(singletonList(6)));
        map.put(53, new HashSet<>(asList(4, 5)));
        map.put(55, new HashSet<>());

        for (Map.Entry<Integer, Set<Integer>> entry : map.entrySet()) {
            for (Integer documentId : entry.getValue()) {
                matrix.add(entry.getKey(), documentId);
            }
        }

        for (int wordId : wordIds) {
            for (int documentId : documentIds) {
                if (map.containsKey(wordId) && map.get(wordId).contains(documentId)) {
                    assertTrue(matrix.contains(wordId, documentId));
                } else {
                    assertFalse(matrix.contains(wordId, documentId));
                }
            }
        }
    }

    @Test
    public void extractLine() {
        Map<Integer, Set<Integer>> map = new HashMap<>();
        map.put(15, new HashSet<>(asList(2, 5)));
        map.put(27, new HashSet<>(singletonList(1)));
        map.put(39, new HashSet<>(asList(1, 3, 4)));
        map.put(43, new HashSet<>(singletonList(6)));
        map.put(53, new HashSet<>(asList(4, 5)));

        for (Map.Entry<Integer, Set<Integer>> entry : map.entrySet()) {
            for (Integer documentId : entry.getValue()) {
                matrix.add(entry.getKey(), documentId);
            }
        }

        assertEquals(Integer.parseInt("010010", 2), matrix.extractLine(15));
        assertEquals(Integer.parseInt("100000", 2), matrix.extractLine(27));
        assertEquals(Integer.parseInt("101100", 2), matrix.extractLine(39));
        assertEquals(Integer.parseInt("000001", 2), matrix.extractLine(43));
        assertEquals(Integer.parseInt("000110", 2), matrix.extractLine(53));
    }

    @Test
    public void saveAndRestore() {
        Map<Integer, Set<Integer>> map = new HashMap<>();
        map.put(15, new HashSet<>(asList(2, 5)));
        map.put(27, new HashSet<>(singletonList(1)));
        map.put(39, new HashSet<>(asList(1, 3, 4)));
        map.put(43, new HashSet<>(singletonList(6)));
        map.put(53, new HashSet<>(asList(4, 5)));

        for (Map.Entry<Integer, Set<Integer>> entry : map.entrySet()) {
            for (Integer documentId : entry.getValue()) {
                matrix.add(entry.getKey(), documentId);
            }
        }

        matrix.save(Paths.get(TEST_INDEX_DIR));

        Matrix restored = new Matrix();
        restored.load(Paths.get(TEST_INDEX_DIR));

        assertEquals(matrix.getDocsQuantity(), restored.getDocsQuantity());
        assertEquals(matrix.getMatrix(), restored.getMatrix());
    }

}
