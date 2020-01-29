import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Params is required");
            return;
        }
        String mode = args[0];
        try {
            if (mode.equals("indexing")) {
                indexing(args[1], args[2]);
            } else if (mode.equals("search")) {
                search(args[1]);
            } else {
                System.out.println("Invalid first param!");
            }
        } catch (IOException e) {
            System.out.println("Cannot read/write file!");
            e.printStackTrace();
        }
    }

    private static void indexing(String documentsFileName, String indexPath) throws IOException {
        File folder = new File(indexPath);
        if (!folder.exists()) {
            folder.mkdir();
        } else if (folder.isFile()) {
            System.out.println("Index argument must be folder or not exist!");
            return;
        }
        FileReader documentsFile = new FileReader(documentsFileName);
        Indexer.writeIndex(documentsFile, indexPath);
    }

    private static void search(String indexPath) throws IOException {

        Index index = new Index();
        index.read(indexPath + "/index");
        SearchClient searchClient = new SearchClient(index);
        DocumentsReader reader = new DocumentsReader(indexPath);
        String query;
        System.out.println("Input query:");
        Scanner scanner = new Scanner(System.in);
        while (!(query = scanner.nextLine()).trim().equals("")) {
            Set<Integer> documentIds = searchClient.findDocuments(query);
            System.out.println(reader.getDocuments(documentIds));

        }
    }
}
