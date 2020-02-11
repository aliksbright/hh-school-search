package HHSchoolSearch.Search;

import HHSchoolSearch.Model.Token;
import HHSchoolSearch.Resources;
import HHSchoolSearch.Search.QueryStructure.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static HHSchoolSearch.Utils.Exceptions.throwFormat;

public class Evaluator
{
    private static Set<Token> evaluateLeaf(QTerm leaf)
    {
        var termString = leaf.getTermString();

        checkStops(List.of(termString));

        return Searcher.searchTerm(termString);
    }

    public static Set<Token> evaluateNode(QOperand node) throws Exception
    {
        if (node instanceof QTerm)
        {
            return evaluateLeaf((QTerm)node);
        }
        else if (node instanceof QMultOperation)
        {
            var mult = (QMultOperation)node;
            switch (mult.getOper())
            {
                case EXACT_PHRASE:  return exactPhrase(mult.getOperands());
                case ENTRY_OR:      return entryOR(mult.getOperands());
                default:
                    throwFormat("Evaluation Error: Unsupported operator '%s'.", mult.getOper().toString());
                    return null;
            }
        }
        else if (node instanceof QBinOperation)
        {
            var bin = (QBinOperation)node;
            switch (bin.getOper())
            {
                case AND: return and(bin.getLeft(), bin.getRight());
                case OR:  return or(bin.getLeft(), bin.getRight());
                case NOT: return not(bin.getLeft(), bin.getRight());
                default:
                    throwFormat("Evaluation Error: Unsupported operator '%s'.", bin.getOper().toString());
                    return null;
            }
        }
        else
        {
            throwFormat("Evaluation Error: Unsupported operation type '%s'.", node.getClass().getName());
            return null;
        }
    }

    private static Set<Token> exactPhrase(List<? extends QOperand> args) throws Exception
    {
        var termStrings = args.stream()
                .map(arg -> ((QTerm)arg).getTermString())
                .collect(Collectors.toList());

        if (termStrings.stream().allMatch(str -> str.equals("*")))
            throwFormat("Invalid Arguments: Empty phrase search.");

        checkStops(termStrings);

        return Searcher.searchExactPhrase(termStrings);
    }

    private static Set<Token> entryOR(List<? extends QOperand> args) throws Exception
    {
        var entriesQtyArg = (QTerm)args.get(0);
        var entriesQty = Integer.parseInt(entriesQtyArg.getTermString());

        var inners = new ArrayList<Set<Token>>();
        for (int i = 1; i < args.size(); i++)
        {
            var qOperand = (QOperand)args.get(i);
            inners.add(evaluateNode(qOperand));
        }

        if (inners.isEmpty())
            throwFormat("Invalid Arguments: Empty ENTRY_OR query.");

        return Searcher.appearingAtLeast(inners, entriesQty);
    }

    private static Set<Token> and(QOperand left, QOperand right) throws Exception
    {
        if (Stream.of(left, right)
                .allMatch(arg -> arg instanceof QTerm))
        {
            var argsStr = Stream.of(left, right)
                    .map(arg -> (QTerm)arg)
                    .map(QTerm::getTermString)
                    .collect(Collectors.toList());

            return Searcher.intersectSimple(argsStr);
        }
        else
        {
            var inners = Arrays.asList(
                    evaluateNode(left),
                    evaluateNode(right)
            );

            return Searcher.intersect(inners);
        }
    }

    private static Set<Token> or(QOperand left, QOperand right) throws Exception
    {
        var inners = Arrays.asList(
                evaluateNode(left),
                evaluateNode(right)
        );

        return Searcher.unite(inners);
    }

    private static Set<Token> not(QOperand left, QOperand right) throws Exception
    {
        var leftRes = evaluateNode(left);

        if (right instanceof QTerm)
        {
            var rightStr = ((QTerm)right).getTermString();
            return Searcher.except(leftRes, List.of(rightStr));
        }
        else
        {
            var rightRes = evaluateNode(right);
            return Searcher.except(leftRes, rightRes);
        }
    }

    private static void checkStops(List<String> terms)
    {
        for (var termString : terms)
            if (Resources.getStopWords().contains(termString))
                System.out.println(String.format("Warning: the term \"%s\" is a stop-word and cannot be found.", termString));
    }
}
