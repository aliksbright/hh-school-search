package HHSchoolSearch.Search;

import HHSchoolSearch.Search.QueryStructure.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static HHSchoolSearch.Utils.Exceptions.throwFormat;
import static HHSchoolSearch.Utils.Lists.dropTokensFromTo;
import static HHSchoolSearch.Utils.Lists.subList;
import static HHSchoolSearch.Utils.Lists.indexOfAny;

public class Parser
{
    public static QOperand parseQuery(String queryString) throws Exception
    {
        var tokens = splitTokens(queryString);
        return parseDeep(tokens);
    }

    private static List<Object> splitTokens(String queryStr)
    {
        // не ищем места смены типа символа (напр. буква/знак пунктуации), а просто пробелами

        var spacedStr = queryStr
                .replaceAll("\\(", " ( ")
                .replaceAll("\\)", " ) ")
                .replaceAll("\\[", " [ ")
                .replaceAll("]", " ] ")
                .replaceAll("<<", " << ")
                .replaceAll(">>", " >> ")
                .replaceAll("~", " ~ ");

        return Arrays.stream(spacedStr.split("\\s+"))
                .filter(Predicate.not(String::isBlank))
                .collect(Collectors.toList());
    }

    private static QOperand parseDeep(List<Object> queryTokens) throws Exception
    {
        var subEnd = queryTokens.indexOf(")");

        // нет закрывающей скобки, вглубь не уходим
        if (subEnd == -1)
        {
            var start = queryTokens.indexOf("(");
            if (start != -1)
                throwFormat("Syntax Error: Missing ')' for '(' at %d (relative position in a (sub)query).", start);
            else return parseFlat(queryTokens);
        }

        var subStart = subList(queryTokens,0, subEnd).lastIndexOf("(");
        if (subStart == -1) throwFormat("Syntax Error: Unexpected ')' at %d (relative position in a (sub)query).", subEnd);

        var subTokens = subList(queryTokens,subStart + 1, subEnd);
        var sub = parseFlat(subTokens);

        dropTokensFromTo(queryTokens, subStart, subEnd + 1);
        queryTokens.add(subStart, sub);

        // не хочу громодить цикл, здесь рекурсия читается легче
        // искать скобки дальше
        return parseDeep(queryTokens);
    }

    private static QOperand parseFlat(List<Object> queryTokens) throws Exception
    {
        angledBrackets(queryTokens);
        squareBrackets(queryTokens);

        tokenizeBins(queryTokens);

        for (var equalBins : priorBins)
            takeBins(queryTokens, equalBins);

        if (!(queryTokens.size() == 1 && queryTokens.get(0) instanceof QOperand))
            throwFormat("Syntax Error: Couldn't resolve the root operation of the (sub)query.");

        return (QOperand)queryTokens.get(0);
    }

    private static void angledBrackets(List<Object> queryTokens) throws Exception
    {
        var phraseEnd = queryTokens.indexOf(">>");

        // нет закрывающей скобки, уходим
        if (phraseEnd == -1)
        {
            var start = queryTokens.indexOf("<<");
            if (start != -1)
                throwFormat("Syntax Error: Missing '>>' for '<<' at %d (relative position in a (sub)query).", start);
            else return;
        }

        var phraseStart = subList(queryTokens,0, phraseEnd).lastIndexOf("<<");
        if (phraseStart == -1) throwFormat("Syntax Error: Unexpected '>>' at %d (relative position in a (sub)query).", phraseEnd);

        var phraseTokens = subList(queryTokens,phraseStart + 1, phraseEnd);
        if (!phraseTokens.stream().allMatch(obj -> obj instanceof String))
            throwFormat("Invalid Arguments: Phrase query at %d (relative position in a (sub)query) must contain only term words.", phraseStart);

        var phraseTerms = phraseTokens.stream()
                .map(token -> new QTerm((String)token))
                .collect(Collectors.toList());

        var phrase = new QMultOperation(QMultOperator.EXACT_PHRASE, phraseTerms);

        dropTokensFromTo(queryTokens, phraseStart, phraseEnd + 1);
        queryTokens.add(phraseStart, phrase);

        // к следующей фразе
        angledBrackets(queryTokens);
    }

    private static void squareBrackets(List<Object> queryTokens) throws Exception
    {
        var orEnd = queryTokens.indexOf("]");

        // нет закрывающей скобки, уходим
        if (orEnd == -1)
        {
            var start = queryTokens.indexOf("[");
            if (start != -1)
                throwFormat("Syntax Error: Missing ']' for '[' at %d (relative position in a (sub)query).", start);
            else return;
        }

        var orStart = subList(queryTokens,0, orEnd).lastIndexOf("[");
        if (orStart == -1) throwFormat("Syntax Error: Unexpected ']' at %d (relative position in a (sub)query).", orEnd);

        var orTokens = subList(queryTokens,orStart + 1, orEnd);

        String qty = "";
        try
        {
            qty = (String)queryTokens.get(orStart - 1);
            Integer.parseInt(qty);
        }
        catch (Throwable th)
        {
            throwFormat("Syntax Error: Expected integer argument before '[' for ENTRY_OR query at %d (relative position in a (sub)query).", orStart);
        }
        orStart -= 1;

        var orOperands = orTokens.stream()
                .map(token -> token instanceof QOperand
                        ? (QOperand)token
                        : new QTerm((String)token))
                .collect(Collectors.toList());
        orOperands.add(0, new QTerm(qty));

        var or = new QMultOperation(QMultOperator.ENTRY_OR, orOperands);

        dropTokensFromTo(queryTokens, orStart, orEnd + 1);
        queryTokens.add(orStart, or);

        // дальше
        squareBrackets(queryTokens);
    }

    private static void tokenizeBins(List<Object> queryTokens)
    {
        IntStream.range(0, queryTokens.size()).forEach(i ->
        {
            var token = queryTokens.get(i);

            if (token instanceof QOperand) return;

            Object newToken;
            switch ((String)token)
            {
                case "AND":
                    newToken = QBinOperator.AND;
                    break;
                case "OR":
                    newToken = QBinOperator.OR;
                    break;
                case "NOT":
                    newToken = QBinOperator.NOT;
                    break;
                default:
                    newToken = new QTerm((String)token);
                    break;
            }

            queryTokens.remove(i);
            queryTokens.add(i, newToken);
        });
    }

    private static void takeBins(List<Object> queryTokens, List<QBinOperator> binsToTake) throws Exception
    {
        var binPos = indexOfAny(queryTokens, binsToTake);
        if (binPos == -1) return;
        var binOper = (QBinOperator)queryTokens.get(binPos);

        if (0 == binPos || binPos == queryTokens.size() - 1)
            throwFormat("Syntax Error: Binary Operator at %d (relative position in a (sub)query) must take two operands.", binPos);

        Object left = queryTokens.get(binPos - 1), right = queryTokens.get(binPos + 1);
        if (!(left instanceof QOperand && right instanceof QOperand))
            throwFormat("Invalid arguments for operator at %d (relative position in a (sub)query).", binPos);

        var bin = new QBinOperation(binOper, (QOperand)left, (QOperand)right);

        dropTokensFromTo(queryTokens, binPos - 1, binPos + 2);
        queryTokens.add(binPos - 1, bin);

        // дальше
        takeBins(queryTokens, binsToTake);
    }

    private static List<List<QBinOperator>> priorBins = Arrays.asList(
            // с одинаковым приоритетом могут оказаться несколько операторов, поэтому списки
            Arrays.asList(QBinOperator.AND),
            Arrays.asList(QBinOperator.OR),
            Arrays.asList(QBinOperator.NOT)
    );

    @Test
    public void test()
    {
        var tests = new ArrayList<String>();
        //tests.add("");
        //tests.add("()()()");
        //tests.add("((()) ())");
        //tests.add("a AND (b OR c) OR d OR (e NOT (f AND g OR h))");
        //tests.add("apple OR mandarins AND 2[abrikos <<peach perfect>> melon]");
        //tests.add("4[A B C D 2[E F G H] J K]");
        for (var input : tests) {
            try {
                var tokens = splitTokens(input);
                var result = parseDeep(tokens);
                result = result;
            } catch (Exception ex) {
                var r = ex.getMessage();
            }
        }
    }
}
