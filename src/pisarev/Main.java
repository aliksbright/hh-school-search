package pisarev;

import pisarev.index.Dictionary;
import pisarev.index.Index;
import pisarev.index.Normalizator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.Set;

import static java.nio.file.attribute.PosixFilePermission.OWNER_READ;
import static java.nio.file.attribute.PosixFilePermission.OWNER_WRITE;


public class Main {
    public static void main(String[] args) {
        String indx = "/Users/ktimika/Projects/hh-search/index";
        String orig = "/Users/ktimika/Projects/hh-search/orig";
        Path indexFile = Paths.get(indx);
        Path originFile = Paths.get(orig);
        if (Files.isDirectory(indexFile))
            indexFile = indx.endsWith("/") ? Paths.get(indx + "/index") : Paths.get(indx + "index");
        try {
            if (checkFiles(indexFile, originFile)) {
                List<String> normalizeLines = Normalizator.normalizeStrings(originFile);
                normalizeLines.forEach(System.out::println);
                Index index = new Index(indexFile);
                index.writeToIndex(normalizeLines);
                index.readFromIndex();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean checkFiles(Path indexFile, Path originFile) throws IOException {
        if (Files.isRegularFile(originFile)) {
            if (!Files.isReadable(originFile))
                Files.setPosixFilePermissions(originFile, Set.of(OWNER_READ));
            if (!Files.isRegularFile(indexFile))
                Files.createFile(indexFile, PosixFilePermissions.asFileAttribute(Set.of(OWNER_READ, OWNER_WRITE)));
            else if(!Files.isReadable(indexFile) || !Files.isWritable(indexFile))
                Files.setPosixFilePermissions(indexFile, Set.of(OWNER_READ, OWNER_WRITE));
            return true;
        }
        return false;
    }
}
