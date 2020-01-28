import java.io.*;
import java.util.*;

public class Main {

    private static Map<String, HashSet<Integer>> indexTerms = new HashMap<>();
    private static Map<Integer, String> indexDocs = new HashMap<>();
    private static Map<String, HashSet<String>> finalIndex = new HashMap<>();

    private static List<String> stopWords = Arrays.asList("a", "able", "about",
            "across", "after", "all", "almost", "also", "am", "among", "an",
            "and", "any", "are", "as", "at", "be", "because", "been", "but",
            "by", "can", "cannot", "could", "dear", "did", "do", "does",
            "either", "else", "ever", "every", "for", "from", "get", "got",
            "had", "has", "have", "he", "her", "hers", "him", "his", "how",
            "however", "i", "if", "in", "into", "is", "it", "its", "just",
            "least", "let", "like", "likely", "may", "me", "might", "most",
            "must", "my", "neither", "no", "nor", "not", "of", "off", "often",
            "on", "only", "or", "other", "our", "own", "rather", "said", "say",
            "says", "she", "should", "since", "so", "some", "than", "that",
            "the", "their", "them", "then", "there", "these", "they", "this",
            "tis", "to", "too", "twas", "us", "wants", "was", "we", "were",
            "what", "when", "where", "which", "while", "who", "whom", "why",
            "will", "with", "would", "yet", "you", "your");

    private static List<String> punctuationMarks = Arrays.asList(",", ".", "?", ":", "!");

    public static void main(String[] args) {
        switch (args[0]) {
            case "INDEX":
                try {
                    index(args[1], args[2]);
                } catch (IOException e) {
                    System.out.println("The error occurs... Maybe you've entered incorrect path for source file?");
                }
                break;
            case "SEARCH":
                try {
                    search(args[1], args[2]);
                } catch (IOException e) {
                    System.out.println("The error occurs... Maybe you've entered incorrect path for index file?");
                }
                break;
            default:
                System.out.println("There are only 2 modes in this program\n" +
                        "For INDEX mode enter: java -jar hh-search-1.0-SNAPSHOT.jar INDEX path-to-index-file path-to-source-file\n" +
                        "For SEARCH mode enter: java -jar hh-search-1.0-SNAPSHOT.jar SEARCH path-to-index-file search-query\n" +
                        "Try again.");
                break;
        }
    }

    private static void index(String pathToIndex, String pathToSourceFile) throws IOException {
        File source = new File(pathToSourceFile);
        parsingInitialFile(source);
        File index = new File(pathToIndex);
        createFinalIndex(index);
    }

    private static void search(String pathToIndex, String searchQuery) throws IOException {
        File index = new File(pathToIndex);
        try (BufferedReader reader = new BufferedReader(new FileReader(index))) {
            ArrayList<String> pair;
            HashMap<String, ArrayList<String>> resultIndex = new HashMap<>();
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                pair = new ArrayList<>(Arrays.asList(line.split(":")));
                resultIndex.put(pair.get(0),
                        new ArrayList<>(Arrays.asList(pair.get(1).substring(1, pair.get(1).length() - 1).split(","))));
            }
            System.out.println("We found " + searchQuery + " in docs:\n");
            for (String foundDoc : resultIndex.get(searchQuery)) {
                System.out.println(foundDoc.trim());
            }
        }
    }

    private static void parsingInitialFile(File source) throws IOException {
        ArrayList<String> tokenizationLine;
        try (BufferedReader reader = new BufferedReader(new FileReader(source))) {
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                tokenizationLine = new ArrayList<>(Arrays.asList(line.split(" ")));
                for (String word : tokenizationLine) {
                    if (stopWords.contains(word)) {
                        continue;
                    }
                    if (punctuationMarks.contains(word.substring(word.length() - 1))) {
                        word = word.substring(0, word.length() - 1);
                    }
                    word = word.toLowerCase();
                    if (finalIndex.containsKey(word)) {
                        finalIndex.get(word).add(line);
                    } else {
                        finalIndex.put(word, new HashSet<>());
                        finalIndex.get(word).add(line);
                    }
                }
            }
        }
    }

    private static void createFinalIndex(File index) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(index))) {
            for (Map.Entry<String, HashSet<String>> term : finalIndex.entrySet()) {
                writer.write(term.getKey() + ":" + term.getValue() + "\n");
            }
        }
    }
}
