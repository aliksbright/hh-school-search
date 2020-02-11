package HHSchoolSearch.Index;

import HHSchoolSearch.Model.Document;
import HHSchoolSearch.Model.Term;
import HHSchoolSearch.Model.Token;
import HHSchoolSearch.Resources;
import HHSchoolSearch.Utils.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Tokenizer
{
    public static List<Token> tokenize(Document document, boolean assign)
    {
        // Получение ресурсов
        var specialsSet = Resources.getSpecialWords();
        var punctuationsSet = Resources.getPunctuations();
        var stopWordsSet = Resources.getStopWords();

        // Приведение к единому регистру
        var rawContent = document.getDocContent().toLowerCase();
        var specialsLow = specialsSet.stream().map(String::toLowerCase).collect(Collectors.toList());
        var stopWordsLow = stopWordsSet.stream().map(String::toLowerCase).collect(Collectors.toSet());

        // Шаг 1: выборка специальных терминов
        var tokensAfterSpecials = tokenizeSpecials(rawContent, specialsLow, 0);
        // Шаг 2: разбивка остального по словам
        var tokens = dropPunctuationsAndSpaces(tokensAfterSpecials, punctuationsSet);
        // Шаг 3: исключение шумовых стоп-слов
        dropStopWords(tokens, stopWordsLow);

        // Связывание токенов с термами
        tokenizeTerms(tokens);

        // Связывание токенов с положением в документе
        var readyTokens = finishTokenizing(tokens, document);

        // Сохранение токенов в объекте документа
        if (assign) document.putTokens(readyTokens);

        return readyTokens;
    }

    private static List<RawToken> tokenizeSpecials(String content, List<String> specials, int currentSpecialIndex)
    {
        if (content.isBlank())
        {
            return new ArrayList<>();
        }

        if (currentSpecialIndex >= specials.size())
        {
            var tokenizedContent = new RawToken(null, content);
            return List.of(tokenizedContent);
        }

        var currentSpecial = specials.get(currentSpecialIndex);
        var currentSpecialRegex = Strings.escapeRegex(currentSpecial);

        var specialTerm = AllTerms.provideTerm(currentSpecial);
        var specialToken = new RawToken(specialTerm, currentSpecial);

        var parts = content.split(currentSpecialRegex, -1);
        var resultingList = new ArrayList<RawToken>();

        for (var part : parts)
        {
            var innerTokens = tokenizeSpecials(part, specials, currentSpecialIndex + 1);

            resultingList.addAll(innerTokens);
            resultingList.add(specialToken);
        }
        resultingList.remove(resultingList.size() - 1);

        var nonEmptyResultingList = resultingList.stream()
                .filter(token -> !token.string.isBlank())
                .collect(Collectors.toList());

        return nonEmptyResultingList;
    }

    private static List<RawToken> dropPunctuationsAndSpaces(List<RawToken> source, Set<String> punctuations)
    {
        // builds /[\sp1p2...pn]+/
        StringBuilder punctuationRegexBld = new StringBuilder("[\\s");
        for (var p : punctuations) punctuationRegexBld.append(Strings.escapeRegex(p));
        punctuationRegexBld.append("]+");

        var punctuationRegex = punctuationRegexBld.toString();
        var result = new ArrayList<RawToken>();

        for (var token : source)
        {
            if (token.isReady()) result.add(token);
            else
            {
                var parts = token.string.split(punctuationRegex);
                var tokens = Arrays.stream(parts)
                        .map(part -> new RawToken(null, part))
                        .filter(part -> !part.string.isBlank())
                        .collect(Collectors.toList());
                result.addAll(tokens);
            }
        }

        return result;
    }

    private static void dropStopWords(List<RawToken> source, Set<String> stopWords)
    {
        var stopTokens = source.stream()
                .filter(token ->
                        !token.isReady() &&
                        stopWords.contains(token.string))
                .collect(Collectors.toList());

        for (var stopToken : stopTokens) stopToken.stop = true;
    }

    private static void tokenizeTerms(List<RawToken> tokens)
    {
        tokens.stream()
                .filter(RawToken::notReady)
                .filter(RawToken::notStop)
                .forEach(token -> token.term = AllTerms.provideTerm(token.string));
    }

    private static List<Token> finishTokenizing(List<RawToken> source, Document document)
    {
        // set positions
        source.stream().sequential()
                .forEach(token -> token.position = source.indexOf(token));

        // remove stop-words
        var pureTokens = source.stream().sequential()
                .filter(RawToken::notStop)
                .collect(Collectors.toList());

        var result = source.stream().sequential()
                .filter(RawToken::notStop)
                .map(rawToken -> new Token(
                        rawToken.position,
                        rawToken.term,
                        document))
                .collect(Collectors.toList());

        return result;
    }

    private static class RawToken
    {
        public boolean stop;
        public int position;

        public Term term;
        public String string;

        public RawToken(Term _term, String _string)
        {
            stop = false;
            term = _term;
            string = _string;
        }

        public boolean isStop() { return stop; }
        public boolean notStop() { return !stop; }

        public boolean isReady() { return term != null; }
        public boolean notReady() { return term == null; }
    }
}
