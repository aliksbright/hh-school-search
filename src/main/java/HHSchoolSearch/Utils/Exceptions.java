package HHSchoolSearch.Utils;

public class Exceptions
{
    public static void throwFormat(String format, Object... args) throws Exception
    {
        var message = String.format(format, args);
        throw new Exception(message);
    }
}
