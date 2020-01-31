package mainClass;

import index.Index;
import index.Dictionary;
import index.Normalizator;
import searcher.Searcher;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.*;

import static java.nio.file.attribute.PosixFilePermission.OWNER_READ;
import static java.nio.file.attribute.PosixFilePermission.OWNER_WRITE;

public class Main {
    public static void main(String[] args) {
        if (args.length == 3 && (args[0].equals("-i") || args[0].equals("-s"))) {

            // создаем индекс
            try {
                Path indexFile = checkIndex(args[1]);
                Index index = new Index(indexFile);
                index.readFromIndex();

                // создаем словарь или загружаем уже имеющийся (словарь должен находиться в той же папке, что и индекс)
                String dict = args[1].replaceAll("\\/(?:.(?!\\/))+$", "/dictionary");
                Dictionary dictionary;
                if (Files.isRegularFile(Paths.get(dict))) {
                    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dict));
                    dictionary = (Dictionary) ois.readObject();
                    ois.close();
                } else {
                    dictionary = new Dictionary();
                }

                //индексация
                if (args[0].equals("-i")) {

                    Path originFile = Paths.get(args[2]);
                    if (Files.isRegularFile(originFile)) {
                        Map<String, String> mapFromOrigFile = Normalizator.normalizeStrings(originFile);
                        index.writeToIndex(mapFromOrigFile);
                        dictionary.put(mapFromOrigFile);
                        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dict.toString()));
                        oos.writeObject(dictionary);
                        oos.close();
                        System.out.println("Indexing is finished!");
                    } else {
                        System.out.println("File for indexing not found!");
                    }
                    // поиск
                } else {
                    Searcher searcher = new Searcher(index, dictionary);
                    searcher.printDocumentsFromIndex(args[2]);
                }
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("Wrong arguments!\nUsage:\n" +
                    "-i <path to index file> <path to file for indexing>\n" +
                    "-s <path to index file> <search request>");
        }
    }

    private static Path checkIndex(String file) throws IOException {
        Path indexFile = Paths.get(file);
        if (Files.isDirectory(indexFile))
            indexFile = file.endsWith("/") ? Paths.get(file + "index") : Paths.get(file + "/index");
        if (!Files.isRegularFile(indexFile)) {
            Files.createFile(indexFile, PosixFilePermissions.asFileAttribute(new HashSet<>(Arrays.asList(OWNER_READ, OWNER_WRITE))));
            System.out.println("Index file created!");
        } else if (!Files.isReadable(indexFile) || !Files.isWritable(indexFile))
            Files.setPosixFilePermissions(indexFile, new HashSet<>(Arrays.asList(OWNER_READ, OWNER_WRITE)));
        return indexFile;
    }
}
