import org.junit.Assert;
import org.junit.Test;
import ru.hh.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class Test1 {

    @Test
    public void whenSplit_thenCorrect() {
        String s = "112; to Baeldu; ng dsasdasd eadfasfd";
        var array = s.split(";", 2);
        var words = array[1].split(" ");
        assertEquals(2,  array.length);
    }

    @Test
    public void parseLong() {
        var str = "1478";
        long num = Long.parseLong(str);
        assertEquals(1478, num);

    }

    @Test
    public void IndexerTest(){
        var loader = new IndexLoader("pophasdog.dat");
        var indexer = new Indexer(loader);
        var documents = new ArrayList<>(List.of(
             (new Document(1,"у попа была собака он её любил")),
              (new Document(2,"она съела кусок мяса он её убил")),
             (new Document(3,"у попа был собака мопед как кусок собака"))));
        indexer.addDocuments(documents);

        var term = indexer.getTerm("собака");
        Assert.assertEquals(Set.of(1L,3L), term.getDocumentIds());
        var positins = term.getDocumentPositions(3);
        Assert.assertNotNull(positins);
        Assert.assertEquals(Arrays.asList(3, 7), positins);
        indexer.SaveIndex();
    }
    @Test
    public void IndexLoadTest(){
        var loader = new IndexLoader("pophasdog.dat");
        var index = loader.loadIndex();
        Assert.assertNotNull(index);
    }

    @Test
    public void documentIndexerTest() {
        var loader = new IndexLoader("thales.dat");
//        var indexer = new Indexer(loader);
//        var documentReader = new DocumentReader("thalesoftwocities.txt");
//        var documentIndexer = new DocumentIndexer(indexer, documentReader);
//        documentIndexer.Index();
//        indexer.SaveIndex();

        var searcher = new Searcher(loader);
        var documents = searcher.SearchDocuments("two");
        Assert.assertEquals(3, documents.size());
    }
}
