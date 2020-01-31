package index;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;

public class Normalizator {

    public Normalizator() {

    }

    public static Map<String, String> normalizeStrings(Path originFile) throws IOException {
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
                // в качестве ключа кладем все что до первого пробела, в качестве значения
                // все что после
                .collect(Collectors.toMap(
                        line -> line.split(" ")[0],
                        line -> line.replaceFirst(".*?\\s", "")
                ));
    }
}
