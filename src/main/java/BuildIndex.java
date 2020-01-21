import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

class BuildIndex {

    private final BufferedReader bufferedReader;

    BuildIndex(BufferedReader bufferedReader) throws IOException {
        this.bufferedReader = bufferedReader;
        buildIndex();
    }

    private void buildIndex() throws IOException{
        System.out.println("Введите расположение индекса :");
        String indexDirectoryPath = bufferedReader.readLine();
        System.out.println("Введите расположение индексируемого файла :");
        String filePath = bufferedReader.readLine();
        File file = new File(filePath);

        Path pathIndex = Paths.get(indexDirectoryPath);
        Directory indexDirectory =
                FSDirectory.open(pathIndex);
        Analyzer analyzer = new
                StandardAnalyzer();
        IndexWriterConfig indexWriterConfig = new
                IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(indexDirectory, indexWriterConfig);

        Document document = new Document();
        FileReader fileReader = new FileReader(file);

        document.add(new TextField("contents", fileReader));
        document.add(new StringField("path", file.getPath(), Field.Store.YES));
        document.add(new StringField("filename", file.getName(), Field.Store.YES));

        indexWriter.addDocument(document);
        indexWriter.close();
        System.out.println("Файл добавлен\n");
    }
}
