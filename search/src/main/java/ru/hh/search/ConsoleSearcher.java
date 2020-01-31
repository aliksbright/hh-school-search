package ru.hh.search;

import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Scanner;

import static ru.hh.search.Util.*;

/**
 * Консольный поисковик.
 * @author Andrey Bukhtoyarov (andreymedoed@gmail.com).
 * @version %Id%.
 * @since 0.1.
 */
public class ConsoleSearcher implements Action {
    /**
     * Строчка с поисковым запросом.
     */
    private String query;

    @Override
    public void start(String[] args) {
        try {
            if (args.length < 2) {
                throw new IndexOutOfBoundsException("Вы ввели не все необходимые данные." +
                        " Запустите программу с ключом -h для помощи.");
            }
            pathChecker(Paths.get(args[1]));
            Scanner scanner = new Scanner(System.in);
            System.out.println("Введите поисковый запрос:");
            this.query = scanner.nextLine();
            Search search = new Search(Paths.get(args[1]), query);
            search.createInvIndex();
            Map<Integer, String> result;
            if (andNot(this.query)) {
                result = search.pSearch(search.andNotSearch());
            } else {
                result = search.pSearch(search.search());
            }
            StringBuilder sb = new StringBuilder();
            result.forEach((id, str) -> sb.append(id).append(" - ").append(str).append(System.lineSeparator()));
            System.out.println(sb.toString());
        } catch (IndexOutOfBoundsException | FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private boolean andNot(String query) {
        return getTokens(query).stream().anyMatch(token -> "and".equals(token) | "not".equals(token));
    }
}
