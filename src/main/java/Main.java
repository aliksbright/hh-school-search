import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        Indexer indexer = new Indexer();
        indexer.getTermPositionDocs(indexer.getTermDocs(indexer.getDocsFromFile("to_index.txt")));

        Map<Integer, Integer> map = new HashMap<>();
        map.put(1,2);
        map.put(1,33);
        System.out.println(map.get(1));
        map.remove(1);
        System.out.println(map.get(1));
        map.remove(1);

    }
}
