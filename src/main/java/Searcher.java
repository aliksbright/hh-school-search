import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Searcher {

    public static List<Integer> search(Path indexPath, String request) {
        return new Index(indexPath).getRevertIndex().getOrDefault(request, new ArrayList<>());
    }
}
