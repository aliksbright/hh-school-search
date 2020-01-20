import java.io.*;
import java.util.*;
import java.util.stream.Stream;

public class Main {

    private static Map<String, HashSet<Integer>> indexTerms = new HashMap<>();
    private static Map<Integer, String> indexDocs = new HashMap<>();

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
                    e.printStackTrace();
                }
                break;
            case "SEARCH":
                try {
                    search(args[1], args[2]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                System.out.println("There's not such mode. Try again.");
                break;
        }
    }

    private static void index(String pathToIndex, String pathToSourceFile) throws IOException {
        File index = new File(pathToIndex);
        File source = new File(pathToSourceFile);
        ArrayList<String> tokenizationLine;
        int docsCount = 0;
        BufferedReader reader = new BufferedReader(new FileReader(source));
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            indexDocs.put(docsCount, line);
            tokenizationLine = new ArrayList<>(Arrays.asList(line.split(" ")));
            for (String word : tokenizationLine) {
                if (stopWords.contains(word)) {
                    continue;
                }
                if (punctuationMarks.contains(word.substring(word.length() - 1))) {
                    word = word.substring(0, word.length() - 1);
                }
                word = word.toLowerCase();
                if (indexTerms.containsKey(word)) {
                    indexTerms.get(word).add(docsCount);
                } else {
                    indexTerms.put(word, new HashSet<>());
                    indexTerms.get(word).add(docsCount);
                }
            }
            docsCount++;
        }
        reader.close();
        BufferedWriter writer = new BufferedWriter(new FileWriter(index));
        for (Map.Entry<String, HashSet<Integer>> term : indexTerms.entrySet()) {
            writer.write(term.getKey() + ":" + term.getValue() + "\n");
        }
        writer.close();
    }

    private static void search(String pathToIndex, String searchQuery) throws IOException {
        File index = new File(pathToIndex);
        BufferedReader reader = new BufferedReader(new FileReader(index));
        ArrayList<String> pair;
        HashMap<String, String> resultIndex = new HashMap<>();
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            pair = new ArrayList<>(Arrays.asList(line.split(":")));
            resultIndex.put(pair.get(0), pair.get(1));
        }
        System.out.println("We found " + searchQuery + " in docs:\n" + resultIndex.get(searchQuery));
    }
}
