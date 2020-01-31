package index;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class DictionaryTest {

    @Test
    public void put() {
        Dictionary dictionary = new Dictionary();

        Map<String, String> map = new HashMap<>();
        map.put("1", "java developer");
        map.put("2", "город самара");
        map.put("3", "город москва");
        map.put("4", "java junior developer");

        dictionary.put(map);
        Map<String, Set<String>> expected = dictionary.getTermDictionary();

        Map<String, Set<String>> actual = new HashMap<>();
        actual.put("java", new HashSet<>(Arrays.asList("1", "4")));
        actual.put("developer", new HashSet<>(Arrays.asList("1", "4")));
        actual.put("город", new HashSet<>(Arrays.asList("2", "3")));
        actual.put("самара", new HashSet<>(Collections.singletonList("2")));
        actual.put("москва", new HashSet<>(Collections.singletonList("3")));
        actual.put("junior", new HashSet<>(Collections.singletonList("4")));

        Assert.assertEquals(expected, actual);
    }
}