package pisarev.index;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Index {
    private Path indexFile;
    private Map<String, String> indexMap;

    public Index(Path indexFile) {
        this.indexFile = indexFile;
        this.indexMap = new HashMap<>(100);
    }

    public void writeToIndex(List<String> lines) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(indexFile.toString(), true));
        for (String line : lines)
            writer.write(line.concat("\n"));
        writer.close();
    }

    public void readFromIndex() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(indexFile.toString()));
        Pattern pattern = Pattern.compile("(^\\d+)\\s+(.*)");
        String line;
        while((line = reader.readLine()) != null) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find())
                this.indexMap.put(matcher.group(1), matcher.group(2));
        }
        reader.close();
    }

    public String getIndexFile() {
        return indexFile.toString();
    }

    public Map<String, String> getIndexMap() {
        return indexMap;
    }
}
