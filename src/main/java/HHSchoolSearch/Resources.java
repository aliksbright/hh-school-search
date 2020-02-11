package HHSchoolSearch;

import HHSchoolSearch.Index.*;
import HHSchoolSearch.Utils.Files;
import HHSchoolSearch.Utils.Strings;

import java.util.HashSet;
import java.util.List;

public class Resources
{
    public static String getPathSpecialWords() { return "/res/dict/special_words.txt"; }
    public static String getPathPunctuations() { return "/res/dict/punctuations.txt"; }
    public static String getPathStopWords() { return "/res/dict/stop_words.txt"; }

    public static String getPathAllDocs() { return "/res/index/all_docs.json"; }
    public static String getPathAllTerms() { return "/res/index/all_terms.json"; }
    public static String getPathInvertedDocs() { return "/res/index/inverted_docs.json"; }
    public static String getPathInvertedTerms() { return "/res/index/inverted_terms.json"; }

    private static HashSet<String> specialWords;
    private static HashSet<String> punctuations;
    private static HashSet<String> stopWords;

    public static HashSet<String> getSpecialWords() { return specialWords; }
    public static HashSet<String> getPunctuations() { return punctuations; }
    public static HashSet<String> getStopWords() { return stopWords; }

    public static boolean initialize()
    {
        System.out.println("Loading resources...");

        try
        {
            loadSpecialWords();
            loadPunctuations();
            loadStopWords();
        }
        catch (Exception ex)
        {
            System.err.print("Couldn't load resources. Error message: ");
            System.err.println(ex.getMessage());
            return false;
        }

        System.out.println("Resources loaded successfully.");
        return true;
    }

    private static void loadSpecialWords() throws Exception
    {
        var path = getPathSpecialWords();
        var lines = Files.readLinesFromFile("." + path);
        var tokens = Strings.splitTokensBySpace(Strings.dropComments(lines));

        specialWords = new HashSet<>(tokens);
    }

    private static void loadPunctuations() throws Exception
    {
        var path = getPathPunctuations();
        var lines = Files.readLinesFromFile("." + path);
        var tokens = Strings.splitTokensBySpace(Strings.dropComments(lines));

        punctuations = new HashSet<>(tokens);
    }

    private static void loadStopWords() throws Exception
    {
        var path = getPathStopWords();
        var lines = Files.readLinesFromFile("." + path);
        var tokens = Strings.splitTokensBySpace(Strings.dropComments(lines));

        stopWords = new HashSet<>(tokens);
    }

    public static boolean saveIndex()
    {
        System.out.println("Saving index...");
        try
        {
            Files.writeLinesToFile(
                    "." + getPathAllDocs(),
                    List.of(Serializer.jsonAllDocs()));
            Files.writeLinesToFile(
                    "." + getPathAllTerms(),
                    List.of(Serializer.jsonAllTerms()));
            Files.writeLinesToFile(
                    "." + getPathInvertedDocs(),
                    List.of(Serializer.jsonInvertedDocs()));
            Files.writeLinesToFile(
                    "." + getPathInvertedTerms(),
                    List.of(Serializer.jsonInvertedTerms()));

            System.out.println("Index saved successfully.");
            return true;
        }
        catch (Exception ex)
        {
            System.err.print("Couldn't save index. Error message: ");
            System.err.println(ex.getMessage());
            return false;
        }
    }

    public static boolean loadIndex()
    {
        System.out.println("Loading index...");
        try
        {
            AllDocs.initialize(
                    Serializer.loadAllDocs(
                            Files.readWholeFile("." + getPathAllDocs())));

            AllTerms.initialize(
                    Serializer.loadAllTerms(
                            Files.readWholeFile("." + getPathAllTerms())));

            InvertedDocs.initialize(
                    Serializer.loadInvertedDocs(
                            Files.readWholeFile("." + getPathInvertedDocs())));

            InvertedTerms.initialize(
                    Serializer.loadInvertedTerms(
                            Files.readWholeFile("." + getPathInvertedTerms())));

            System.out.println("Index loaded successfully.");
            return true;
        }
        catch (Exception ex)
        {
            System.err.print("Couldn't load index. Error message: ");
            System.err.println(ex.getMessage());
            return false;
        }
    }
}
