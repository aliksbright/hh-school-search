package ru.hh;
import org.apache.commons.cli.*;

public class IndexSearcher {
    public static void main(String[] args) throws Exception {
        Options options = new Options();

        var indexDirectory = new Option("i", "indexdir", true, "directory index contained");
        indexDirectory.setRequired(false);
        options.addOption(indexDirectory);

        var indexDocument = new Option("d", "documents", true, "input file document path");
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

        var inputFilePath = cmd.getOptionValue("documents");
        var indexFilePath = cmd.getOptionValue("indexdir");
        var loader = new IndexLoader(indexFilePath);
        if (!empty(inputFilePath) && !empty(indexFilePath)){
            var indexer = new Indexer(loader);
            var documentReader = new DocumentReader(inputFilePath);
            var documentIndexer = new DocumentIndexer(indexer, documentReader);
            documentIndexer.Index();
            indexer.SaveIndex();
        }
        var query = cmd.getOptionValue("query");
        if(!empty(query)){
            var searcher = new Searcher(loader);
            var documents = searcher.SearchDocuments(query);
            for(var d: documents){
                System.out.println(d.getText());
            }
        }
    }

    public static boolean empty( final String s ) {
        // Null-safe, short-circuit evaluation.
        return s == null || s.trim().isEmpty();
    }
}
