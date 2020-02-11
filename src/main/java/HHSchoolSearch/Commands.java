package HHSchoolSearch;

import HHSchoolSearch.Index.Indexer;
import HHSchoolSearch.Search.Querier;
import HHSchoolSearch.Utils.Export;
import HHSchoolSearch.Utils.Files;

import java.util.List;

public class Commands
{
    public static void execute(String[] args)
    {
        if (args == null || args.length == 0)
            help(args);

        else switch (args[0])
        {
            case "help":    help(args);     break;
            case "init":    init(args);     break;
            case "index":   index(args);    break;
            case "search":  search(args);   break;
            default:        error(args);    break;
        }
    }

    private static void error(String[] args)
    {
        System.out.println(String.format("Command \"%s\" was not recognized.", args[0]));
        System.out.println();

        help(null);
    }

    private static void init(String[] args)
    {
        boolean
                dict = args.length < 2 || args[1].equals("dict"),
                index = args.length < 2 || args[1].equals("index"),
                unknown = !(dict || index);

        try
        {
            if (dict)
            {
                System.out.print("init dict ...");

                for (var path : List.of(
                        Resources.getPathSpecialWords(),
                        Resources.getPathPunctuations(),
                        Resources.getPathStopWords()
                ))
                    Export.exportResourceFile(path, "." + path);

                System.out.println("OK");
            }

            if (index)
            {
                System.out.print("init index ...");

                for (var path : List.of(
                        Resources.getPathAllDocs(),
                        Resources.getPathAllTerms(),
                        Resources.getPathInvertedDocs(),
                        Resources.getPathInvertedTerms()
                ))
                    Export.exportResourceFile(path, "." + path);

                System.out.println("OK");
            }

            if (unknown)
            {
                System.err.println(String.format("Unknown directory \"%s\".", args[1]));
            }
        }
        catch (Exception ex)
        {
            System.err.println(String.format("FAILED. Message: %s", ex.getMessage()));
        }
    }

    private static void index(String[] args)
    {
        if (!Resources.initialize())
        {
            System.err.println("Failed to load resources. Check validity of the files in \"./res\" or try creating samples by \"init\" command.");
        }
        else if (!Resources.loadIndex())
        {
            System.err.println("Failed to load index. Check validity of the files in \"./res\" or try creating samples by \"init\" command.");
        }
        else
        {
            if (args.length < 2)
            {
                help(args);
                return;
            }

            try
            {
                var path = args[1];
                var lines = Files.readLinesFromFile(path);
                Indexer.indexLinesAsDocs(path, lines);
            }
            catch (Exception ex)
            {
                System.err.println(String.format("Failed indexing documents. Message: %s", ex.getMessage()));
            }
            finally
            {
                Resources.saveIndex();
            }
        }
    }

    private static void search(String[] args)
    {
        if (!Resources.initialize())
        {
            System.err.println("Failed to load resources. Check validity of the files in \"./res\" or try creating samples by \"init\" command.");
        }
        else if (!Resources.loadIndex())
        {
            System.err.println("Failed to load index. Check validity of the files in \"./res\" or try creating samples by \"init\" command.");
        }
        else
        {
            System.out.println();

            if (args.length < 2)
            {
                help(args);
                return;
            }

            try
            {
                var query = args[1];
                Querier.performQuery(query);
            }
            catch (Throwable th)
            {
                System.err.println(String.format("Failed completing search. Message: %s", th.getMessage()));
            }
        }
    }

    private static void help(String[] args)
    {
        System.out.println("Available params: help, init, init dict, init index, index <filepath>, search \"<query>\"");
    }
}
