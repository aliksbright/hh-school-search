package index;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;


public class Index {
    private Path indexFile;
    private Map<String, String> indexMap = new HashMap<>(100);

    public Index(Path indexFile) {
        this.indexFile = indexFile;
    }

    public Map<String, String> normalizeStrings(Path originFile) throws IOException {
        return Files.lines(originFile)
                // фультруем от пустых строк
                .filter(line -> !line.equals(""))
                .map(line -> {
                    // удаляем все кроме букв английского и русского алфавита, цифр и пробелов
                    line = line.replaceAll("[^\\d\\wа-яА-ЯёЁ\\s]", "").trim();
                    // заменяем несколько пробелов на один, переводим символы в нижний регистр
                    line = line.replaceAll("\\s++", " ").toLowerCase();
                    return line;
                })
                 /*
                 в качестве ключа кладем все что до первого пробела, в качестве значения
                 все что после
                 */
                .collect(Collectors.toMap(
                        line -> line.split(" ")[0],
                        line -> line.replaceFirst(".*?\\s", "")
                ));
    }

    public void writeToIndex(Map<String, String> lines) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(indexFile.toString()));
        for (Map.Entry<String, String> entry : lines.entrySet())
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
