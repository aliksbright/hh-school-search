package HHSchoolSearch.Search;

import HHSchoolSearch.Index.AllDocs;
import HHSchoolSearch.Index.AllTerms;
import HHSchoolSearch.Model.Token;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Querier
{
    public static void performQuery(String query) throws Exception
    {
        var root = Parser.parseQuery(query);
        var results = Evaluator.evaluateNode(root);

        var grouped = groupResults(results);
        printResults(grouped);
    }

    private static Map<Integer, List<Token>> groupResults(Set<Token> allTokens)
    {
        return allTokens.stream()
                .collect(Collectors.groupingBy(Token::getDocumentId));
    }

    private static void printResults(Map<Integer, List<Token>> groupedTokens)
    {
        if (groupedTokens.isEmpty())
            System.out.println("<No results found>");

        else for (var docId : groupedTokens.keySet())
        {
            var doc = AllDocs.getDoc(docId);
            System.out.println(String.format("In Document \"%s\" (\"%s\"):", doc.getDocName(), doc.getDocContent()));

            for (var token : groupedTokens.get(docId))
            {
                var term = AllTerms.findTerm(token.getTermId());
                System.out.println(String.format(" - At %d: \"%s\"", token.getPosition(), term.getTermString()));
            }
        }
    }
}
