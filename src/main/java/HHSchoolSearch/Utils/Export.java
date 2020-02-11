package HHSchoolSearch.Utils;

import HHSchoolSearch.Resources;

import java.nio.file.CopyOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Export
{
    public static void exportResourceFile(String srcPath, String destPath) throws Exception
    {
        var source = Files.openResourceStream(srcPath);
        var dest = Paths.get(destPath);

        java.nio.file.Files.createDirectories(dest.getParent());
        java.nio.file.Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
    }
}
