package HHSchoolSearch.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Files
{
    public static List<String> readLinesFromResource(String resourcePath) throws Exception
    {
        try (var stream = openResourceStream(resourcePath))
        {
            return readLinesFromStream(stream);
        }
    }

    public static List<String> readLinesFromFile(String filePath) throws Exception
    {
        var path = Paths.get(filePath);
        return java.nio.file.Files.lines(path).collect(Collectors.toList());
    }

    public static String readWholeFile(String filePath) throws Exception
    {
        var path = Paths.get(filePath);
        return java.nio.file.Files.lines(path)
                .collect(Collectors.joining());
    }

    public static void writeLinesToFile(String filePath, Iterable<String> text) throws Exception
    {
        var path = Paths.get(filePath);
        java.nio.file.Files.deleteIfExists(path);
        java.nio.file.Files.write(path, text, StandardOpenOption.CREATE);
    }

    public static boolean checkFileExists(String fileName)
    {
        try
        {
            var path = Paths.get(fileName);
            return java.nio.file.Files.exists(path);
        }
        catch (Exception ex)
        {
            return false;
        }
    }


    static InputStream openResourceStream(String fileName) throws Exception
    {
        var cls = HHSchoolSearch.Main.class;
        var stream = cls.getResourceAsStream(fileName);

        if (stream == null)
            throw new Exception(String.format("Couldn't open the resource file \"%s\".", fileName));
        return stream;
    }

    static List<String> readLinesFromStream(InputStream stream) throws Exception
    {
        try (var scanner = new Scanner(stream))
        {
            scanner.useDelimiter("(\r\n|\n\r|\r|\n)");
            return scanner.tokens().collect(Collectors.toList());
        }
    }
}
