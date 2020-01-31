package index;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Index {
    private Path indexFile;
    private Map<String, String> indexMap = new HashMap<>(100);

    public Index(Path indexFile) {
        this.indexFile = indexFile;
    }

    public void writeToIndex(Map<String, String> lines) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(indexFile.toString()));
        indexMap = Stream.concat(indexMap.entrySet().stream(), lines.entrySet().stream())
                // создаем объединенную карту, если ключи совпадают, то записываем в индекс значение
                // из карты которая передана аргументом метода (обновляем индекс)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v2));
        for (Map.Entry<String, String> entry : indexMap.entrySet())
            writer.write(entry.getKey() + " " + entry.getValue() + "\n");
        writer.close();
    }

    public void readFromIndex() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(indexFile.toString()));
        Pattern pattern = Pattern.compile("(^.*?)\\s(.*)");
        String line;
        while((line = reader.readLine()) != null) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find())
                indexMap.put(matcher.group(1), matcher.group(2));
        }
        reader.close();
    }

    public String getIndexFile() {
        return indexFile.toString();
    }

    public Map<String, String> getIndexMap() {
        return indexMap;
    }

    public void setIndexFile(Path indexFile) {
        this.indexFile = indexFile;
    }

    public void setIndexMap(Map<String, String> indexMap) {
        this.indexMap = indexMap;
    }
}
