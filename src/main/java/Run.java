import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Run {

    public static void main(String[] args) throws IOException {
        Arguments arguments = new Arguments(args);

        if (arguments.regime.equals("index")) {
            Index index = new Index(arguments.getDocsPath());
            index.saveIndex(arguments.indexDir, index.makeIndex());
        }
        else {
            //Parse args and define regime of work
            arguments.prepareQuery(args);

            Set<Integer> result = new HashSet<>();
            Search search = new Search(arguments.indexDir);
            switch (arguments.searchRegime) {
                case 0: result = search.simpleSearch(arguments.query);
                break;
                case 1: result = search.boolSearch(arguments.query);
                break;
                case 2: result = search.phraseSearch(arguments.query);
                break;
                case 3: result = search.orSearch(arguments.query, arguments.N);
                break;
            }
            System.out.printf("Query: %s\nResult: %s", arguments.query, result);
        }
    }
}
