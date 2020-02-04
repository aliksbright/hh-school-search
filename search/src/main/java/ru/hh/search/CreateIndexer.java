package ru.hh.search;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
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
public class CreateIndexer {

    private final Writer writer;

    public CreateIndexer(Writer writer) {
        this.writer = writer;
    }

    public void create(String[] args) {
        try {
            Path from = Paths.get(args[1]);
            pathChecker(from);
            new Indexer(writer).execute(from);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Вы ввели не все необходимые данные. Запустите программу с ключом -h для помощи.");
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }
}
