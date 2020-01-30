import java.io.IOException;

public class TestIndex {
    @org.junit.Test
    public void makeIndex() throws IOException {
        String path = "docs.txt";
        Index index = new Index(path);
        var dict = index.makeIndex();
        System.out.println(dict);
    }

    @org.junit.Test
    public void saveIndex() throws IOException {
        String pathDocs = "docs.txt";
        String pathIndex = "index.txt";
        Index index = new Index(pathDocs);
        var dict = index.makeIndex();
        index.saveIndex(pathIndex, dict);
    }

    @org.junit.Test
    public void readIndex() throws IOException {
        String path = "index.txt";
        Index index = new Index(path);
        System.out.println(index.readIndex());
    }
}
