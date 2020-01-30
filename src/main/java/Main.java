import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0)
            System.out.println("Arguments are required");
        else if (args[0].equals("index")) {
            Path indexDir = Paths.get(args[1]);
            Path filePath = Paths.get(args[2]);
            new IndexBuilder(indexDir, filePath).buildIndex();
        } else if (args[0].equals("search")) {
            Path indexPath = Paths.get(args[1]);
            String request = args[2];
            System.out.println("This word is contained in the following docs: " +
                    Searcher.search(indexPath, request.toLowerCase()));
        }
    }
}
