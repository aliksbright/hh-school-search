import org.junit.Test;

import java.io.IOException;

public class TestSearch {

    @Test
    public void EmptySearch() throws IOException {
        String pathToIndex = "index.txt";
        String query = "";
        Search search = new Search(pathToIndex);
        var res = search.simpleSearch(query);
        System.out.printf("Query: %s\nResult: %s\n", query, res);
    }
    @Test
    public void SimpleSearch() throws IOException {
        String pathToIndex = "index.txt";
        String query = "JAVA";
        Search search = new Search(pathToIndex);
        var res = search.simpleSearch(query);
        System.out.printf("Query: %s\nResult: %s\n", query, res);
    }

    @Test
    public void AndSearch() throws IOException {
        String pathToIndex = "index.txt";
        String query = "java and developer";
        Search search = new Search(pathToIndex);
        var res = search.boolSearch(query);
        System.out.printf("Query: %s\nResult: %s\n", query, res);
    }

    @Test
    public void NotSearch() throws IOException {
        String pathToIndex = "index.txt";
        String query = "not scientist";
        Search search = new Search(pathToIndex);
        var res = search.boolSearch(query);
        System.out.printf("Query: %s\nResult: %s\n", query, res);
    }

    @Test
    public void NotAndSearch() throws IOException {
        String pathToIndex = "index.txt";
        String query = "not master and data";
        Search search = new Search(pathToIndex);
        var res = search.boolSearch(query);
        System.out.printf("Query: %s\nResult: %s\n", query, res);
    }

    @Test
    public void PhraseSearch() throws IOException {
        String pathToIndex = "index.txt";
        String query = "java developer";
        Search search = new Search(pathToIndex);
        var res = search.phraseSearch(query);
        System.out.printf("Query: %s\nResult: %s\n", query, res);
    }

    @Test
    public void OrSearch() throws IOException {
        String pathToIndex = "index.txt";
        String query = "java OR developer OR программист";
        Search search = new Search(pathToIndex);
        var res = search.orSearch(query, 2);
        System.out.printf("Query: %s\nResult: %s\n", query, res);
    }
}
