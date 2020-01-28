package main;


import java.io.IOException;
import java.util.HashMap;

public class Main {
    public static final String DEFAULT_LIMIT = "1";
    public static final String WRONG_PARAMS = "Wrong params";
    public static final String INDEX_FILE_NAME = "index.txt";
    public static final String DOC_FILE_NAME = "doc_file.txt";
    private static int limit;
    private static final String WRONG_PARAMS_MESSAGE = "Неправильно переданы параметры. \n" +
            "Формат параметров: \n" +
            "-mode \"INDEX / SEARCH\" \n" +
            "-i \"путь к индексу\" \n" +
            "-d \"путь к документу\" \n" +
            "-q \"свободный запрос, можно использовать AND/OR/NOT\" \n" +
            "-l для запроса с OR минимальное число пар, которое должно входить в искомый документ, по-умолчанию 1";
    private static HashMap<String, String> arguments = new HashMap<>();
    private static String indexPath, docFilePath;

    public static void main(String[] args) throws IOException {
        for (int i = 0; i < args.length - 1; i++) {
            switch (args[i]) {
                case "-q":
                    arguments.put("-q", args[i + 1]);
                    limit = Integer.parseInt(arguments.getOrDefault("-l", DEFAULT_LIMIT));
                    i++;
                    break;
                case "-i":
                    indexPath = args[i + 1] + INDEX_FILE_NAME;
                    docFilePath = args[i + 1] + DOC_FILE_NAME;

                default:
                    arguments.put(args[i], args[i + 1]);
                    i++;
            }

        }

        switch (arguments.getOrDefault("-mode", WRONG_PARAMS)) {
            case "INDEX":
                try {
                    UpdateIndex updateIndex = new UpdateIndex();
                    updateIndex.addWordsToIndex(indexPath, docFilePath, arguments.get("-d"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "SEARCH":
                Search search = new Search();
                search.search(indexPath, docFilePath, arguments.get("-q"), limit);
                break;
            default:
                System.out.println(WRONG_PARAMS_MESSAGE);
                System.exit(0);
        }
    }


}
