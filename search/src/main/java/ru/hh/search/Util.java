package ru.hh.search;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Утилитный класс.
 * @author Andrey Bukhtoyarov (andreymedoed@gmail.com).
 * @version %Id%.
 * @since 0.1.
 */
public class Util {
    /**
     * Из переданной строки:
     * выбрасывает предлоги,
     * выбрасывает знаки препинания и т.д.
     * приводит буквы к прописным.
     * @param line строка для токенизации
     * @return список оставшихся слов
     */
    public static List<String> getTokens(String line) {
        Set<String> preps = Set.of("по", "на", "в", "с", "к", "у", "над","под", "перед", "при", "для", "во");
        return Arrays.stream(line.split(" "))
                .map(String::toLowerCase)
                .map(token -> token.replaceAll("[^a-zA-Zа-яА-Я+\"]", ""))
                .filter(token -> !preps.contains(token))
                .collect(Collectors.toList());
    }

    /**
     * Проверяет, существует ли указанный путь?
     * @param paths путь
     * @throws FileNotFoundException если путь не существует.
     */
    public static void pathChecker(Path... paths) throws FileNotFoundException {
        for (Path path : paths) {
            if (!path.toFile().exists()) {
                throw new FileNotFoundException("Файла или директории не существует " + path.toString());
            }
        }
    }
}
