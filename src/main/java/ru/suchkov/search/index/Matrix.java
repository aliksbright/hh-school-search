package ru.suchkov.search.index;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static java.lang.String.format;
import static java.util.Collections.singletonList;

/**
 * Класс матрицы инцедентности (инвертированного индекса)
 */
public class Matrix {

    /**
     * Название файла, под которым сохраняется, и из которого восстанавливается
     * объект матрицы инцидентности (инвертированный индекс)
     */
    private static final String MATRIX_FILE_NAME = "matrix";

    /**
     * Обратный индекс
     */
    private Map<Integer, Set<Integer>> matrix;

    /**
     * Количество документов (он же, id максимального документа. Документы
     * маркируются id начиная с единицы)
     */
    private int docsQuantity;

    public Matrix() {
        matrix = new HashMap<>();
        docsQuantity = 0;
    }

    public Map<Integer, Set<Integer>> getMatrix() {
        return matrix;
    }

    public int getDocsQuantity() {
        return docsQuantity;
    }

    /**
     * Вносит в матрицу инцидентности информацию о том, что слово с id = wordId
     * встречалось в документе с id = documentId
     * @param wordId: id слова
     * @param documentId: id документа
     */
    public void add(int wordId, int documentId) {
        if (matrix.containsKey(wordId)) {
            matrix.get(wordId).add(documentId);
        } else {
            matrix.put(wordId, new HashSet<>(singletonList(documentId)));
        }
        docsQuantity = Math.max(docsQuantity, documentId);
    }

    /**
     * Проверяет вхождение слова в документ
     * @param wordId: id слова
     * @param documentId: id документа
     * @return возвращает true, если wordId есть в документе documentId, иначе false
     */
    public boolean contains(int wordId, int documentId) {
        return matrix.containsKey(wordId) && matrix.get(wordId).contains(documentId);
    }

    /**
     * Возвращает строку из матрицы в виде числа,
     * двоичное представление которого отражает положение 1 и 0 в этой строке таблицы
     * @param wordId: id слова
     * @return  возвращает число, соответствующее битовому представлению строки матницы
     */
    public int extractLine(int wordId) {
        if (!matrix.containsKey(wordId) || matrix.get(wordId) == null || matrix.get(wordId).isEmpty())
            return 0;

        StringBuilder builder = new StringBuilder();
        Set<Integer> docs = matrix.get(wordId);
        for (int i = 1; i <= docsQuantity; i++) {
            if (docs.contains(i)) {
                builder.append(1);
            } else {
                builder.append(0);
            }
        }
        return Integer.parseInt(builder.toString(), 2);
    }

    /**
     * Сохраняет в файл матрицу инцедентности (инвертированный индекс) по переданному путю
     * @param folder: путь к директории, в которой создатся файл и сохранит значения
     */
    public void save(Path folder) {
        try (FileWriter fileWriter = new FileWriter(folder.resolve(MATRIX_FILE_NAME).toFile());
             BufferedWriter writer = new BufferedWriter(fileWriter)){
            writer.write(new Gson().toJson(matrix, Map.class));
            writer.newLine();
            writer.write(Integer.toString(docsQuantity));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Восстанавливает матрицу инцедентности (инвертированный индекс) по переданному путю
     * @param folder: путь к директории, в которой находится файл с индексом
     */
    public void load(Path folder) {
        try {
            List<String> data = Files.readAllLines(folder.resolve(MATRIX_FILE_NAME));
            Type type = new TypeToken<HashMap<Integer, HashSet<Integer>>>(){}.getType();
            matrix = new Gson().fromJson(data.get(0), type);
            docsQuantity = Integer.parseInt(data.get(1));
        } catch (IOException e) {
            System.out.println(format("File %s does'not exist in %s", MATRIX_FILE_NAME, folder));
        }
    }
}
