package homework.search;

import org.junit.Test;

public class TestSearch {
    String filePath = "E:/Java/IdeaProjects/index_file1.txt";
    @Test
    public void searchOneWord() {
        Searcher searcher = new Searcher(filePath);
        String query = "javA python".toLowerCase();
        searcher.analyzerSearch(query);//1,2,3,4
    }

    @Test
    public void searchAnd() {
        Searcher searcher = new Searcher(filePath);
        String query = "java AND SQL".toLowerCase();
        searcher.analyzerSearch(query); //2, 4
    }

    @Test
    public void searchAnd1() {
        Searcher searcher = new Searcher(filePath);
        String query = "java AND SQL AND kotlin".toLowerCase();
        searcher.analyzerSearch(query); //только 2
    }

    @Test
    public void searchAndNot() {
        Searcher searcher = new Searcher(filePath);
        String query = "java AND SQL NOT kotlin".toLowerCase();
        searcher.analyzerSearch(query); //только 4
    }

    @Test
    public void searchNot() {
        Searcher searcher = new Searcher(filePath);
        String query = "java NOT Python".toLowerCase();
        searcher.analyzerSearch(query); //только 2, 4
    }

    @Test
    public void searchNot2() {
        Searcher searcher = new Searcher(filePath);
        String query = "NOT java Python".toLowerCase();
        searcher.analyzerSearch(query); //только 3
    }

    @Test
    public void searchPhrase() {
        Searcher searcher = new Searcher(filePath);
        String query = "\"javA-developer\"".toLowerCase();
        searcher.analyzerSearch(query);//2
    }
}
