package HHSchoolSearch.Search;

import HHSchoolSearch.Index.AllDocs;
import HHSchoolSearch.Index.AllTerms;
import HHSchoolSearch.Index.InvertedDocs;
import HHSchoolSearch.Index.InvertedTerms;
import HHSchoolSearch.Model.Token;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/*
 Этот поиск работает дольше и сложнее, чем если бы он возвращал список найденных документов, а не их токенов.
 Однако, такой результат информативнее: сразу видно, где именно в документах сработал запрос.
 Польза - например, подсветить найденные токены в документе, как это делают все поисковики.
 То есть, не придется потом в найденном документе еще раз выполнять поиск, чтобы найти в нем нужное место.
*/
public class Searcher
{
    // поиск одного слова
    public static Set<Token> searchTerm(String termString)
    {
        var term = AllTerms.findTerm(termString.toLowerCase());

        if (term == null) return new HashSet<>();
        return InvertedTerms.getTermEntries(term);
    }

    // поиск точной фразы
    // "*" в termString - любое слово
    public static Set<Token> searchExactPhrase(List<String> termStrings)
    {
        // выкидываем null-ы с начала списка
        while (termStrings.size() > 0 && termStrings.get(0).equals("*")) termStrings.remove(0);
        assert !termStrings.isEmpty(): "Error: attempted to exact search an empty phrase.";

        // Ищем токены с первым термом фразы
        // Возможная оптимизация: начинать проверку фразы не по первому терму, а по терму с наименьшим числом токенов
        var firstTerm = AllTerms.findTerm(termStrings.get(0));
        var firstTokens = InvertedTerms.getTermEntries(firstTerm);

        // Проверка фразы
        var phraseLength = termStrings.size();
        var matchingTokens = firstTokens.stream().filter(token ->
        {
            var doc = AllDocs.getDoc(token.getDocumentId());
            var pos = token.getPosition();

            // (pos + i)-ое слово документа совпадает с i-ым словом фразы
            return IntStream.range(1, phraseLength)
                    .filter(i -> !termStrings.get(i).equals("*")) // пропуск любых слов ("*")
                    .allMatch(i ->
                    {
                        var iToken = doc.getTokenAt(pos + i);
                        if (iToken == null) return false; // либо стоп-слово, либо за пределами документа

                        var iTerm = AllTerms.findTerm(iToken.getTermId());
                        return iTerm.getTermString().equals(termStrings.get(i).toLowerCase());
                    });
        }).collect(Collectors.toSet());

        return matchingTokens;
    }

    // обычный OR
    public static Set<Token> unite(List<Set<Token>> innerMatches)
    {
        return innerMatches.stream()
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    // обычный AND (для подзапросов)
    public static Set<Token> intersect(List<Set<Token>> innerMatches)
    {
        // Оптимизация: начинаем искать пересечение с наименьших множеств
        List<Set<Integer>> innerMatchesDocs =
                innerMatches.stream()
                        .map(tokens -> tokens.stream()
                                .map(Token::getDocumentId)
                                .collect(Collectors.toSet()))
                        .sorted(Comparator.comparingInt(Set::size))
                        .collect(Collectors.toList());

        // Находим пересечение по документам
        var docsIntersection = innerMatchesDocs.isEmpty()
                ? new HashSet<Integer>()
                : innerMatchesDocs.get(0);
        innerMatchesDocs.stream().skip(1).forEach(docsIntersection::retainAll);

        // Возвращаем токены пересечения
        return innerMatches.stream()
                .flatMap(Set::stream)
                .filter(token -> docsIntersection.contains(token.getDocumentId()))
                .collect(Collectors.toSet());
    }

    // Более оптимизированный простой AND
    // Оптимизация: дополнительный инвертированный индекс InvertedDocs
    // => проще искать пересечение по документам (минус лишняя операция distinct в токенах)
    // Еще так можно OR с минимальными вхождениями оптимизировать
    public static Set<Token> intersectSimple(List<String> termStrings)
    {
        // просто термы
        var terms = termStrings.stream()
                .map(AllTerms::findTerm)
                .collect(Collectors.toSet());

        // доки для каждого терма
        var termDocs = terms.stream()
                .map(InvertedDocs::getTermEntries)
                .sorted(Comparator.comparingInt(Set::size))
                .collect(Collectors.toList());

        // токены для каждого терма
        var allTokens = terms.stream()
                .map(InvertedTerms::getTermEntries)
                .collect(Collectors.toList());

        // находим пересечение по документам
        var docsIntersection = termDocs.isEmpty()
                ? new HashSet<Integer>()
                : termDocs.get(0);
        termDocs.stream().skip(1).forEach(docsIntersection::retainAll);

        // возвращаем токены только из доков пересечения
        return allTokens.stream()
                .flatMap(Set::stream)
                .filter(token -> docsIntersection.contains(token.getDocumentId()))
                .collect(Collectors.toSet());
    }

    // NOT <termString...> v1.0
    public static Set<Token> except(Set<Token> matches, List<String> termStrings)
    {
        var termsToExcept = termStrings.stream()
                .map(AllTerms::findTerm)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        var docsToExcept = termsToExcept.stream()
                .map(InvertedTerms::getTermEntries)
                .flatMap(Set::stream)
                .map(Token::getDocumentId)
                .collect(Collectors.toSet());

        var retainedMatches = matches.stream()
                .filter(token -> !docsToExcept.contains(token.getDocumentId()))
                .collect(Collectors.toSet());

        return retainedMatches;
    }

    // NOT (...) v2.0
    public static Set<Token> except(Set<Token> from, Set<Token> ex)
    {
        var exDocs = ex.stream()
                .map(Token::getDocumentId)
                .collect(Collectors.toSet());

        return from.stream()
                .filter(token -> !exDocs.contains(token.getDocumentId()))
                .collect(Collectors.toSet());
    }

    // OR с минимальным количеством вхождений
    // при minEntries == innerMatches.size() - обычный AND (intersection)
    // при minEntries == 1 - обычный OR (union)
    public static Set<Token> appearingAtLeast(List<Set<Token>> innerMatches, int minEntries)
    {
        // подзапрос-токен => подзапрос-документ
        List<Set<Integer>> innerMatchesDocs =
                innerMatches.stream()
                        .map(tokens -> tokens.stream()
                                .map(Token::getDocumentId)
                                .collect(Collectors.toSet()))
                        .collect(Collectors.toList());

        // документ - число вхождений
        Map<Integer, Long> docEntries =
                innerMatchesDocs.stream()
                        .flatMap(Set::stream)
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        // документы, прошедшие лимит
        Set<Integer> appearedDocs =
                docEntries.keySet().stream()
                        .filter(doc -> docEntries.get(doc) >= minEntries)
                        .collect(Collectors.toSet());

        // все токены документов, прошедших лимит
        return innerMatches.stream()
                .flatMap(Set::stream)
                .filter(token -> appearedDocs.contains(token.getDocumentId()))
                .collect(Collectors.toSet());
    }

    // Оптимизацированный простой OR с минимальным количеством вхождений
    public static Set<Token> appearingAtLeastSimple(List<String> termStrings, int minEntries)
    {
        // просто термы
        var terms = termStrings.stream()
                .map(String::toLowerCase)
                .map(AllTerms::findTerm)
                .collect(Collectors.toSet());

        // доки для каждого терма
        var termDocs = terms.stream()
                .map(InvertedDocs::getTermEntries)
                .sorted(Comparator.comparingInt(Set::size))
                .collect(Collectors.toList());

        // токены для каждого терма
        var allTokens = terms.stream()
                .map(InvertedTerms::getTermEntries)
                .collect(Collectors.toList());

        // документ - число вхождений
        Map<Integer, Long> docEntries =
                termDocs.stream()
                        .flatMap(Set::stream)
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        // документы, прошедшие лимит
        Set<Integer> appearedDocs =
                docEntries.keySet().stream()
                        .filter(doc -> docEntries.get(doc) >= minEntries)
                        .collect(Collectors.toSet());

        // все токены документов, прошедших лимит
        return allTokens.stream()
                .flatMap(Set::stream)
                .filter(token -> appearedDocs.contains(token.getDocumentId()))
                .collect(Collectors.toSet());
    }
}
