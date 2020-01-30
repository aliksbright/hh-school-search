package ru.hh.search;

import java.util.HashMap;
import java.util.Map;

/**
 * Главный класс программы.
 * @author Andrey Bukhtoyarov (andreymedoed@gmail.com).
 * @version %Id%.
 * @since 0.1.
 */
public class StartSearcher {

    private final String[] args;

    private final Map<String, Action> actions;

    public StartSearcher(String[] args) {
        this.args = args;
        this.actions = new HashMap<>();
        fillActions();
    }

    private void fillActions() {
        this.actions.put("-i", new ConsoleIndexer());
        this.actions.put("-s", new ConsoleSearcher());
        this.actions.put("-h", new Help());
    }

    public void start() {
        this.actions.getOrDefault(this.args.length != 0 ? this.args[0] : "-h", new Help()).start(this.args);
    }

    public static void main(String[] args) {
        new StartSearcher(args).start();
    }
}
