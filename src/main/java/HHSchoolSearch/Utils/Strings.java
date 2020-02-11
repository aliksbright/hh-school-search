package HHSchoolSearch.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Strings
{
    public static List<String> dropComments(List<String> list)
    {
        return list.stream()
                .map(line -> line.replaceAll("(^|\\s)#[^#].*$", " "))
                .collect(Collectors.toList());
    }

    public static List<String> splitTokensBySpace(List<String> list)
    {
        return list.stream()
                .flatMap(str -> Arrays.stream(str.split("[ \t]+")))
                .filter(Predicate.not(String::isBlank))
                .collect(Collectors.toList());
    }

    public static String escapeRegex(String str)
    {
        var specials = ".$^{[(|)]}*+?\\";

        var builder = new StringBuilder();
        for (var c : str.toCharArray())
        {
            if (specials.indexOf(c) >= 0) builder.append('\\');
            builder.append(c);
        }

        return builder.toString();
    }

    public static List<String> splitTokensByRegex(List<String> list, String regex)
    {
        return list.stream()
                .flatMap(str -> Arrays.stream(str.split(regex)))
                .filter(Predicate.not(String::isBlank))
                .collect(Collectors.toList());
    }

    public static List<String> dropSubs(String source, List<Integer> subStarts, List<Integer> subEnds)
    {
        assert subStarts.size() == subEnds.size() : "Danila, ты что крейзи?";

        var subsQty = subStarts.size();
        if (subsQty == 0) return List.of(source);

        var results = Arrays.asList(source.substring(0, subStarts.get(0)));
        for (int i = 1; i <= subsQty - 2; i++)
            results.add(source.substring(subEnds.get(i - 1) + 1, subStarts.get(i)));
        results.add(source.substring(subEnds.get(subsQty - 1), source.length()));

        return results;
    }
}
