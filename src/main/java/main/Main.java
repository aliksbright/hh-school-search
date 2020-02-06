package main;


import java.io.IOException;
import java.util.HashMap;

public class Main {
    private static final String DEFAULT_LIMIT = "1";
    private static final String WRONG_PARAMS = "Wrong params";
    private static final String INDEX_FILE_NAME = "index.txt";
    private static final String DOC_FILE_NAME = "doc_file.txt";
    private static int limit;
    private static final String WRONG_PARAMS_MESSAGE = "Неправильно переданы параметры. \n" +
            "Формат параметров: \n" +
            "-mode \"INDEX / SEARCH\" \n" +
            "-i \"путь к каталогу с индексом\" \n" +
            "-d \"путь к документу\" \n" +
            "-q \"свободный запрос, можно использовать AND/OR/NOT\" \n" +
            "-l для запроса с OR минимальное число пар, которое должно входить в искомый документ, по-умолчанию 1";

    public static void main(String[] args) throws IOException {
        HashMap<String, String> arguments = new HashMap<>();
        String indexPath = null, docFilePath = null;
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
                if (!arguments.containsKey("-d") || (indexPath == null)) {
                    System.out.println(WRONG_PARAMS_MESSAGE);
                    break;
                }
                try {
                    UpdateIndex updateIndex = new UpdateIndex();
                    updateIndex.addWordsToIndex(indexPath, docFilePath, arguments.get("-d"));
                } catch (IOException e) {
                    System.out.println(WRONG_PARAMS_MESSAGE);
                    e.printStackTrace();
                }
                break;
            case "SEARCH":
                if (!arguments.containsKey("-q") || (indexPath == null)) {
                    System.out.println(WRONG_PARAMS_MESSAGE);
                    break;
                }
                Search search = new Search();
                search.search(indexPath, docFilePath, arguments.get("-q"), limit);
                break;
            default:
                System.out.println(WRONG_PARAMS_MESSAGE);
                System.exit(0);
        }
    }
}
