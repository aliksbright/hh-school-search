package main;


import main.Utils.Search;
import main.Utils.UpdateIndex;

import java.io.IOException;
import java.util.HashMap;

public class Main {
    private static int limit = 0;
    private static final String PARAMS_MESSAGE = "Формат параметров: \n" +
            "-mode \"INDEX / SEARCH\" \n" +
            "-i \"путь к индексу\" \n" +
            "-d \"путь к документу\" \n" +
            "-q \"свободный запрос, можно использовать AND/OR/NOT\" \n" +
            // tbd "-qf \"фразовый запрос\" \n" +
            "-l для запроса с OR минимальное число пар, которое должно входить в искомый документ, по-умолчанию 1";
    private static HashMap<String, String> arguments = new HashMap<>();

    public static void main(String[] args) throws IOException {
        for (int i = 0; i < args.length - 1; i++) {
            switch (args[i]) {
                case "-q":
                    arguments.put("-q", args[i + 1]);
                    limit = Integer.parseInt(arguments.getOrDefault("-l", "1"));
                    i++;
                    break;
                case "-qf":
                    arguments.put(args[i], args[i + 1]);
                    limit = -1;
                    i++;
                    break;
                default:
                    arguments.put(args[i], args[i + 1]);
                    i++;
            }

        }

        switch (arguments.getOrDefault("-mode", "Wrong params")) {
            case "INDEX":
                try {
                    UpdateIndex.addWordsToIndex(arguments.get("-i"), arguments.get("-d"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "SEARCH":
                Search.search(arguments.get("-i"), arguments.get("-q"), limit);
                break;
            default:
                System.out.println("Неправильно переданы параметры. \n" + PARAMS_MESSAGE);
                System.exit(0);
        }
    }


}
