import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

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
                if (args.length < 3) {
                    System.out.println("The error occurs... You've entered less arguments than required\n" +
                            "For INDEX mode enter: java -jar hh-search-1.0-SNAPSHOT.jar INDEX path-to-index-file path-to-source-file\n" +
                            "For SEARCH mode enter: java -jar hh-search-1.0-SNAPSHOT.jar SEARCH path-to-index-file search-query\n" +
                            "Try again.");
                    break;
                }
                try {
                    index(args[1], args[2]);
                } catch (IOException e) {
                    System.out.println("The error occurs... Maybe you've entered incorrect path for source file?");
                }
                break;
            case "SEARCH":
                if (args.length < 3) {
                    System.out.println("The error occurs... You forgot to enter any arguments\n" +
                            "For INDEX mode enter: java -jar hh-search-1.0-SNAPSHOT.jar INDEX path-to-index-file path-to-source-file\n" +
                            "For SEARCH mode enter: java -jar hh-search-1.0-SNAPSHOT.jar SEARCH path-to-index-file search-query\n" +
                            "Try again.");
                    break;
                }
                ArrayList<String> queryArgs = new ArrayList<>();
                queryArgs.addAll(Arrays.asList(args).subList(2, args.length));
                try {
                    search(args[1], queryArgs);
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

    private static void search(String pathToIndex, ArrayList<String> queryArgs) throws IOException {
        File index = new File(pathToIndex);
        switch (queryArgs.size()) {
            case 0:
                System.out.println("You forgot to enter your search query\n" +
                        "You can do it after path to index: java -jar hh-search-1.0-SNAPSHOT.jar SEARCH path-to-index-file SEARCH-QUERY");
                break;
            case 1:
                wordOrPhraseChoice(index, queryArgs);
                break;
            case 2:
                System.out.println("Sorry, but you can search only by one these variants\n" +
                        "SEARCH BY SINGLE WORD (all docs WITH word): java -jar hh-search-1.0-SNAPSHOT.jar SEARCH path-to-index-file word\n" +
                        "SEARCH BY PHRASE (all docs WITH 'my phrase'): java -jar hh-search-1.0-SNAPSHOT.jar SEARCH path-to-index-file 'my phrase'\n" +
                        "SEARCH BY TWO WORDS USING AND (all docs WITH word1 AND word2): java -jar hh-search-1.0-SNAPSHOT.jar SEARCH path-to-index-file word1 AND word2\n" +
                        "SEARCH BY TWO WORDS USING OR (all docs WITH word1 OR word2): java -jar hh-search-1.0-SNAPSHOT.jar SEARCH path-to-index-file word1 OR word2\n" +
                        "SEARCH BY TWO WORDS USING NOT (all docs WITH word1 NOT word2): java -jar hh-search-1.0-SNAPSHOT.jar SEARCH path-to-index-file word1 NOT word2\n" +
                        "Now you've entered two-args search query so we are using only your first arg:\n");
                wordOrPhraseChoice(index, queryArgs);
                break;
            case 3:
                logicOperationChoice(index, queryArgs);
                break;
            default:
                System.out.println("Sorry, but you can search only by one these variants\n" +
                        "SEARCH BY SINGLE WORD (all docs WITH word): java -jar hh-search-1.0-SNAPSHOT.jar SEARCH path-to-index-file word\n" +
                        "SEARCH BY PHRASE (all docs WITH 'my phrase'): java -jar hh-search-1.0-SNAPSHOT.jar SEARCH path-to-index-file 'my phrase'\n" +
                        "SEARCH BY TWO WORDS USING AND (all docs WITH word1 AND word2): java -jar hh-search-1.0-SNAPSHOT.jar SEARCH path-to-index-file word1 AND word2\n" +
                        "SEARCH BY TWO WORDS USING OR (all docs WITH word1 OR word2): java -jar hh-search-1.0-SNAPSHOT.jar SEARCH path-to-index-file word1 OR word2\n" +
                        "SEARCH BY TWO WORDS USING NOT (all docs WITH word1 NOT word2): java -jar hh-search-1.0-SNAPSHOT.jar SEARCH path-to-index-file word1 NOT word2\n" +
                        "Now you've entered more than three args search query so we will try to use only first three args:\n");
                logicOperationChoice(index, queryArgs);
                break;
        }
    }

    private static void wordOrPhraseChoice(File index, ArrayList<String> queryArgs) throws IOException {
        HashSet<String> result = new HashSet<>();
        if (queryArgs.get(0).contains(" ")) {
            ArrayList<String> phraseWords = new ArrayList<>(Arrays.asList(queryArgs.get(0).split(" ")));
            for (String word : phraseWords) {
                result.addAll(searchByTerm(index, word).stream()
                        .map(String::trim)
                        .collect(Collectors.toList()));
            }
            result.removeIf(phrase -> !phrase.toLowerCase().contains(queryArgs.get(0)));
            System.out.println("We found '" + queryArgs.get(0) + "' in docs:\n");
            for (String foundDoc : result) {
                System.out.println(foundDoc);
            }
        } else {
            result = searchByTerm(index, queryArgs.get(0));
            System.out.println("We found " + queryArgs.get(0) + " in docs:\n");
            for (String foundDoc : result) {
                System.out.println(foundDoc.trim());
            }
        }
    }

    private static void logicOperationChoice(File index, ArrayList<String> queryArgs) throws IOException {
        HashSet<String> result = new HashSet<>();
        switch (queryArgs.get(1).toUpperCase()) {
            case "AND":
                for (String phrase : searchByTerm(index, queryArgs.get(0)).stream()
                        .map(String::trim)
                        .collect(Collectors.toList())) {
                    if (phrase.toLowerCase().contains(queryArgs.get(0)) && phrase.toLowerCase().contains(queryArgs.get(2))) {
                        result.add(phrase);
                    }
                }
                for (String phrase : searchByTerm(index, queryArgs.get(2)).stream()
                        .map(String::trim)
                        .collect(Collectors.toList())) {
                    if (phrase.toLowerCase().contains(queryArgs.get(0)) && phrase.toLowerCase().contains(queryArgs.get(2))) {
                        result.add(phrase);
                    }
                }
                System.out.println("We found '" + queryArgs.get(0) +
                        " " + queryArgs.get(1) +
                        " " + queryArgs.get(2) + "' in docs:\n");
                for (String foundDoc : result) {
                    System.out.println(foundDoc);
                }
                break;
            case "OR":
                result.addAll(searchByTerm(index, queryArgs.get(0)).stream()
                        .map(String::trim)
                        .collect(Collectors.toList()));
                result.addAll(searchByTerm(index, queryArgs.get(2)).stream()
                        .map(String::trim).collect(Collectors.toList()));
                System.out.println("We found '" + queryArgs.get(0) +
                        " " + queryArgs.get(1) +
                        " " + queryArgs.get(2) + "' in docs:\n");
                for (String foundDoc : result) {
                    System.out.println(foundDoc);
                }
                break;
            case "NOT":
                for (String phrase : searchByTerm(index, queryArgs.get(0)).stream()
                        .map(String::trim).collect(Collectors.toList())) {
                    if (!phrase.toLowerCase().contains(queryArgs.get(2))) {
                        result.add(phrase);
                    }
                }
                System.out.println("We found '" + queryArgs.get(0) +
                        " " + queryArgs.get(1) +
                        " " + queryArgs.get(2) + "' in docs:\n");
                for (String foundDoc : result) {
                    System.out.println(foundDoc);
                }
                break;
            default:
                System.out.println("Sorry, but you can search only by one these variants\n" +
                        "SEARCH BY SINGLE WORD (all docs WITH word): java -jar hh-search-1.0-SNAPSHOT.jar SEARCH path-to-index-file word\n" +
                        "SEARCH BY PHRASE (all docs WITH 'my phrase'): java -jar hh-search-1.0-SNAPSHOT.jar SEARCH path-to-index-file 'my phrase'\n" +
                        "SEARCH BY TWO WORDS USING AND (all docs WITH word1 AND word2): java -jar hh-search-1.0-SNAPSHOT.jar SEARCH path-to-index-file word1 AND word2\n" +
                        "SEARCH BY TWO WORDS USING OR (all docs WITH word1 OR word2): java -jar hh-search-1.0-SNAPSHOT.jar SEARCH path-to-index-file word1 OR word2\n" +
                        "SEARCH BY TWO WORDS USING NOT (all docs WITH word1 NOT word2): java -jar hh-search-1.0-SNAPSHOT.jar SEARCH path-to-index-file word1 NOT word2\n" +
                        "Now you've entered three-args search query but without AND/OR/NOT so we are using only your first arg:\n");
                wordOrPhraseChoice(index, queryArgs);
                break;
        }
    }

    private static HashSet<String> searchByTerm(File index, String term) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(index))) {
            ArrayList<String> pair;
            HashMap<String, HashSet<String>> resultIndex = new HashMap<>();
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                pair = new ArrayList<>(Arrays.asList(line.split(":")));
                resultIndex.put(pair.get(0),
                        new HashSet<>(Arrays.asList(pair.get(1).substring(1, pair.get(1).length() - 1).split(","))));
            }
            return resultIndex.get(term);
        }
    }
}
