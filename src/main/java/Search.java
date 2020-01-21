import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class Search {

    private final BufferedReader bufferedReader;

    Search(BufferedReader bufferedReader) throws Exception {
        this.bufferedReader = bufferedReader;
        List<Document> list = searchFiles();
        for (Document d : list
             ) {
            System.out.println(d.get("filename"));
        }
        System.out.println();
    }

    private List<Document> searchFiles() throws IOException, ParseException {
        String inField = "contents";
        System.out.println("Введите расположение индекса: ");
        String indexDirectoryPath = bufferedReader.readLine();
        System.out.println("Введите запрос: ");
        String queryString = bufferedReader.readLine();
        Analyzer analyzer = new
                StandardAnalyzer();
        Query query = new QueryParser(inField, analyzer)
                .parse(queryString);
        File file = new File(indexDirectoryPath);
        if (!file.exists())
           return null;
        Directory indexDirectory = FSDirectory
                .open(Paths.get(indexDirectoryPath));
        IndexReader indexReader = DirectoryReader
                .open(indexDirectory);
        IndexSearcher searcher = new IndexSearcher(indexReader);
        TopDocs topDocs = searcher.search(query, 10);
        return Arrays.stream(topDocs.scoreDocs)
                .map(scoreDoc -> {
                    try {
                        return searcher.doc(scoreDoc.doc);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .collect(Collectors.toList());
    }
}
