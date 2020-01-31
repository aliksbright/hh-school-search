import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    private static ByteArrayOutputStream output = new ByteArrayOutputStream();

    @BeforeAll
    public static void setUpStreams() {
        System.setOut(new PrintStream(output));
    }

    @AfterAll
    public static void cleanUpStreams() {
        System.setOut(null);
    }

    @Test
    public void runProgramWithoutArgs() {
        String[] args = new String[0];
        String expectedResult = "The error occurs... You've entered less arguments than required\n" +
                "For INDEX mode enter: java -jar hh-search-1.0-SNAPSHOT.jar INDEX path-to-index-file path-to-source-file\n" +
                "For SEARCH mode enter: java -jar hh-search-1.0-SNAPSHOT.jar SEARCH path-to-index-file search-query\n" +
                "Try again.\n";
        Main.main(args);
        assertEquals(expectedResult, output.toString());
    }

    @Test
    public void runProgramInSearchModeUsingIncorrectIndexPath() {
        String[] args = {"SEARCH", "src/test/resources/indexx.txt", "java"};
        String expectedResult = "The error occurs... Maybe you've entered incorrect path for index file?\n";
        Main.main(args);
        assertEquals(expectedResult, output.toString());
    }

    @Test
    public void searchSingleWord() {
        String[] args = {"SEARCH", "src/test/resources/index.txt", "java"};
        String expectedResult = "We found java in docs:\n" +
                "\n" +
                "Java Developer\n" +
                "Java Android Developer\n";
        Main.main(args);
        assertEquals(expectedResult, output.toString());
    }

    @Test
    public void searchPhrase() {
        String[] args = {"SEARCH", "src/test/resources/index.txt", "java developer"};
        String expectedResult = "We found 'java developer' in docs:\n" +
                "\n" +
                "Java Developer\n";
        Main.main(args);
        assertEquals(expectedResult, output.toString());
    }

    @Test
    public void searchTwoWordsUsingAnd() {
        String[] args = {"SEARCH", "src/test/resources/index.txt", "java", "and", "android"};
        String expectedResult = "We found 'java and android' in docs:\n" +
                "\n" +
                "Java Android Developer\n";
        Main.main(args);
        assertEquals(expectedResult, output.toString());
    }

    @Test
    public void searchTwoWordsUsingOr() {
        String[] args = {"SEARCH", "src/test/resources/index.txt", "java", "or", "android"};
        String expectedResult = "We found 'java or android' in docs:\n" +
                "\n" +
                "Java Developer\n" +
                "Java Android Developer\n";
        Main.main(args);
        assertEquals(expectedResult, output.toString());
    }

    @Test
    public void searchTwoWordsUsingNot() {
        String[] args = {"SEARCH", "src/test/resources/index.txt", "java", "not", "android"};
        String expectedResult = "We found 'java not android' in docs:\n" +
                "\n" +
                "Java Developer\n";
        Main.main(args);
        assertEquals(expectedResult, output.toString());
    }
}