package HHSchoolSearch.Utils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Lists
{
    public static <T> List<T> subList(List<T> theList, int startIndex, int stopIndex)
    {
        return theList.stream()
                .skip(startIndex)
                .limit(stopIndex - startIndex)
                .collect(Collectors.toList());
    }

    public static void dropTokensFromTo(List<Object> theList, int startInclusive, int stopExclusive) throws Exception
    {
        if (!(0 <= startInclusive && startInclusive < stopExclusive && stopExclusive <= theList.size()))
            Exceptions.throwFormat("Invalid arguments (dropTokensFromTo).");

        var repeats = stopExclusive - startInclusive;
        IntStream.range(0, repeats)
                .forEach(__ -> theList.remove(startInclusive));
    }

    public static int indexOfAny(List<?> theList, List<?> toFind)
    {
        for (int i = 0; i < theList.size(); i++)
            if (toFind.contains(theList.get(i)))
                return i;
        return -1;
    }
}
