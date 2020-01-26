package ru.suchkov.search.index;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

public class Dictionary {

    /**
     * Название файла, под которым сохраняется, и из которого восстанавливается объект словаря
     */
    private static final String DICTIONARY_FILE_NAME = "dictionary";

    private Map<String, Integer> dictionary;
    private int size;

    public Dictionary() {
        dictionary = new HashMap<>();
        size = 0;
    }

    public Map<String, Integer> getDictionary() {
        return dictionary;
    }

    public int getSize() {
        return size;
    }

    public void add(String word) {
        if (!dictionary.containsKey(word)) {
            size++;
            dictionary.put(word, size);
        }
    }

    public int getKey(String word) {
        return dictionary.getOrDefault(word, 0);
    }

    /**
     * Сохраняет в файл словарь по переданному путю
     * @param folder: путь к директории, в которой создатся файл и сохранит значения
     */
    public void save(Path folder) {
        try (FileWriter fileWriter = new FileWriter(folder.resolve(DICTIONARY_FILE_NAME).toFile());
             BufferedWriter writer = new BufferedWriter(fileWriter)){
            writer.write(new Gson().toJson(dictionary, HashMap.class));
            writer.newLine();
            writer.write(Integer.toString(size));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Восстанавливает словарь по переданному путю
     * @param folder: путь к директории, в которой находится файл с индексом
     */
    public void load(Path folder) {
        try {
            List<String> data = Files.readAllLines(folder.resolve(DICTIONARY_FILE_NAME));
            Type type = new TypeToken<HashMap<String, Integer>>(){}.getType();
            dictionary = new Gson().fromJson(data.get(0), type);
            size = Integer.parseInt(data.get(1));
        } catch (IOException e) {
            System.out.println(format("File %s does'not exist in %s", DICTIONARY_FILE_NAME, folder));
        }
    }
}
