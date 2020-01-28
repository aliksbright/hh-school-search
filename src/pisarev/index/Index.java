package pisarev.index;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class Index {
    private Path indexFile;

    public Index(Path indexFile) {
        this.indexFile = indexFile;
    }

    public void writeToIndex(List<String> lines) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(indexFile.toString(), true));
        for (String line : lines)
            writer.write(line.concat("\n"));
        writer.close();
    }

    public String getIndexFile() {
        return indexFile.toString();
    }
}
