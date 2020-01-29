package Search;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;


public class Main {

    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("Enter valid parameters: Path_to_file Search_Term");
            return;
        }
        String pathToSource = args[0];
        String searchTerm = args[1];
        File sourceFile = new File(pathToSource);
        if (!sourceFile.exists()) {
            System.out.println("Please, enter a correct path to a file");
            return;
        }
        File indexFile = new File(pathToSource + ".idx");
        if (!indexFile.exists()) {
            System.out.println("An index file hasn't been found, indexing...");
            indexing(pathToSource);
            System.out.println("The index file has been created at " + pathToSource + ".idx");
        }
        TreeSet<Long> numDocWithTerm = readIndex(pathToSource, searchTerm);
        if (numDocWithTerm.size() == 0) {
            System.out.println("No documents found for : " + searchTerm);
            return;
        }
        System.out.println("Docs with " + searchTerm + ":");
        readDocs(pathToSource, numDocWithTerm);
    }

    private static void readDocs(String pathToSource, TreeSet<Long> numDocWithTerm) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(pathToSource))) {
            for (long numDoc = 1; numDoc <= numDocWithTerm.last(); numDoc++) {
                if (numDocWithTerm.contains(numDoc)) {
                    System.out.println(bufferedReader.readLine());
                } else
                    bufferedReader.readLine();
            }
        } catch (IOException e) {
            System.out.println("Can't locate a source file");
            e.printStackTrace();
        }
    }

    private static TreeSet<Long> readIndex(String pathToSource, String term) {
        String doc;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(pathToSource + ".idx"))) {
            while ((doc = bufferedReader.readLine()) != null) {
                if (doc.contains(" " + term + " :")) {
                    return Arrays.stream(doc.split(":")[1].split(" "))
                            .map(Long::valueOf)
                            .collect(Collectors.toCollection(() ->
                                    new TreeSet<>(Comparator.comparing(Long::valueOf))));
                }
            }
        } catch (IOException e) {
            System.out.println("Can't read an index file");
            e.printStackTrace();
        }
        return new TreeSet<>();
    }

    private static void indexing(String pathToSource) {
        Long docNumber = 0L;
        String doc;
        Map<String, HashSet<Long>> index = new HashMap<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(pathToSource))) {
            while ((doc = bufferedReader.readLine()) != null) {
                docNumber++;
                for (String term : doc.replaceAll("\\p{P}", "").split(" ")) {
                    if (index.containsKey(term)) {
                        index.get(term).add(docNumber);
                    } else {
                        index.put(term, new HashSet<>(Collections.singleton(docNumber)));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Can't read the source file " + pathToSource);
            e.printStackTrace();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathToSource + ".idx"))) {
            for (String term : index.keySet())
                writer.write(" " + term + " :" + index.get(term).stream().map(String::valueOf)
                        .collect(Collectors.joining(" ")) + "\n");
        } catch (IOException e) {
            System.out.println("Can't write an index file to " + pathToSource + ".idx");
            e.printStackTrace();
        }
    }

}
