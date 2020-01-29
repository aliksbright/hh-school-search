package ru.hh;
import org.apache.commons.cli.*;

public class IndexSearcher {
    public static void main(String[] args) throws Exception {
        Options options = new Options();

        var indexDirectory = new Option("id", "indexdir", true, "directory index contained");
        indexDirectory.setRequired(false);
        options.addOption(indexDirectory);

        var indexDocument = new Option("dp", "documents", true, "input file document path");
        indexDocument.setRequired(false);
        options.addOption(indexDocument);

        var searchQuery = new Option("q", "query", true, "search string");
        searchQuery.setRequired(false);
        options.addOption(searchQuery);

        var parser = new DefaultParser();
        var formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
        }

        var inputFilePath = cmd.getOptionValue("indexdir");
        System.out.println(inputFilePath);
        System.out.println("->trace");
    }
}
