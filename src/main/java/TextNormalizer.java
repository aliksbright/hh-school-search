import java.util.Arrays;

public class TextNormalizer {

    public static String cleanOutPunct(String s) {
        return s.replaceAll("\\p{Punct}"," ");
    }

    public static String toLowerCase(String s) {
        return s.toLowerCase();
    }

    public static String[] split(String s) {
        return s.split("\\s+");
    }

    public static String[] deleteOneLetterWords(String[] words) {
        return (String[]) Arrays.stream(words).map(word -> word.length() > 1).toArray();
    }
}
