package homework.search;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class TestSearch {
    String filePath = "src/test/java/homework/search/index.txt";

    @Test
    public void searchOneWord() {
        Searcher searcher = new Searcher(filePath);
        String query = " java";
        List<Integer> docId = searcher.searchDocs(query);
//        System.out.println(docId);
        assertTrue(docId.containsAll(Arrays.asList(1,2,4,5,7)));
    }

    @Test
    public void searchAnd() {
        Searcher searcher = new Searcher(filePath);
        String query = "java AND SQL в";
        List<Integer> docId = searcher.searchDocs(query);
        assertTrue(docId.containsAll(Arrays.asList(2,4,5,7)));
    }

    @Test
    public void searchAnd1() {
        Searcher searcher = new Searcher(filePath);
        String query = "java AND SQL AND kotlin";
        List<Integer> docId = searcher.searchDocs(query);
        assertTrue(docId.containsAll(Arrays.asList(2,5,7)));
    }

    @Test
    public void searchAndNot() {
        Searcher searcher = new Searcher(filePath);
        String query = "java AND SQL NOT kotlin";
        List<Integer> docId = searcher.searchDocs(query);
        assertTrue(docId.containsAll(Arrays.asList(4)));
    }

    @Test
    public void searchNot() {
        Searcher searcher = new Searcher(filePath);
        String query = "java NOT Python";
        List<Integer> docId = searcher.searchDocs(query);
        assertTrue(docId.containsAll(Arrays.asList(2,4,5,7)));
    }

    @Test
    public void searchNot2() {
        Searcher searcher = new Searcher(filePath);
        String query = "NOT java Python";
        List<Integer> docId = searcher.searchDocs(query);
        assertTrue(docId.containsAll(Arrays.asList(3)));
    }

    @Test
    public void searchPhrase() {
        Searcher searcher = new Searcher(filePath);
        String query = "\"javA-developer\"";
        List<Integer> docId = searcher.searchDocs(query);
//        System.out.println(docId);
        assertTrue(docId.containsAll(Arrays.asList(2)));
    }

//    @Test
//    public void searchPhrase1() {
//        Searcher searcher = new Searcher(filePath);
//        String query = "\"developer в Москве\"";
//        List<Integer> docId = searcher.searchDocs(query);
//        System.out.println(docId);
//        assertTrue(docId.containsAll(Arrays.asList(2,5,6,7)));
//    }

    @Test
    public void searchNullPhrase() {
        Searcher searcher = new Searcher(filePath);
        String query = "\"  \"";
        List<Integer> docId = searcher.searchDocs(query);
        assertTrue(docId.isEmpty());
    }
}
