package ru.hh.search;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import static ru.hh.search.Util.pathChecker;

/**
 * Консольный индексатор.
 * @author Andrey Bukhtoyarov (andreymedoed@gmail.com).
 * @version %Id%.
 * @since 0.1.
 */
public class ConsoleIndexer implements Action {
    private final String[] args;

    public ConsoleIndexer(String[] args) {
        this.args = args;
    }

    @Override
    public void start() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(getPath(this.args[2]).toFile()))) {
            new CreateIndexer(writer).create(this.args);
        } catch (IndexOutOfBoundsException emptyArgs) {
            System.out.println("Вы ввели не все необходимые данные. Запустите программу с ключом -h для помощи.");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private Path getPath(String path) throws FileNotFoundException {
        Path to = Path.of(path);
        pathChecker(to);
        return to.toFile().isDirectory() ?
                FileSystems.getDefault().getPath(to.toString(), "index.xml") : to;
    }
}
