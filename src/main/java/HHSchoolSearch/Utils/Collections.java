package HHSchoolSearch.Utils;

import java.util.HashSet;
import java.util.Set;

public class Collections
{
    public static <T> Set<T> union(Set<T> left, Set<T> right)
    {
        var union = new HashSet<T>();

        union.addAll(left);
        union.addAll(right);

        return union;
    }
}
