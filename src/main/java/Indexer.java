import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Indexer {

    public static void writeIndex(FileReader documentsFile, String indexPath) throws IOException {
        BufferedReader documents = new BufferedReader(documentsFile);
        Index index = new Index();
        DocumentsWriter documentsWriter = new DocumentsWriter(indexPath);
        Integer id = 0;
        String document;
        while ((document = documents.readLine()) != null) {
            documentsWriter.addDocument(id, document);
            index.addDocument(id, tokenizing(stemmingAndFiltering(document)));
            id++;
        }
        index.save(indexPath + "/index");
        documentsWriter.close();
    }

    //Прототип с примитивными правками, в реальности должен быть словарь стоп-слов и преобразование словоформы
    private static String stemmingAndFiltering(String document) {
        return document.trim().toLowerCase().replaceAll("and|not|a|the|\\(|\\)|,|\\.|;", " ");
    }

    private static List<String> tokenizing(String document) {
        return Arrays.asList(document.split("\\s+"));
    }

}
