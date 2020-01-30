package ru.hh.search;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;

import static ru.hh.search.Util.pathChecker;

/**
 * Консольный индексатор.
 * @author Andrey Bukhtoyarov (andreymedoed@gmail.com).
 * @version %Id%.
 * @since 0.1.
 */
public class ConsoleIndexer implements Action {
    /**
     * Путь с папкой или файлом дляиндексации.
     */
    private Path from;
    /**
     * Путь для файла индекса.
     */
    private Path to;

    @Override
    public void start(String[] args) {
        try {
            this.from = Paths.get(args[1]);
            this.to = Paths.get(args[2]);
            pathChecker(from, to);
            if (to.toFile().isDirectory()) {
                to = FileSystems.getDefault().getPath(to.toString(), "index.xml");
            }
            try (PrintWriter writer = new PrintWriter(to.toFile())) {
                new Indexer(from, writer).execute();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Вы ввели не все необходимые данные. Запустите программу с ключом -h для помощи.");
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}
