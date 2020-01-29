package ru.hh;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class DocumentReader {

    private String documentPath;
    public DocumentReader(String documentPath){
        this.documentPath = documentPath;
    }

    // documents is csv file documentid; text
    public List<Document> GetDocuments() {
        var documents = new ArrayList<Document>();
        try (Stream<String> stream = Files.lines( Paths.get(documentPath), StandardCharsets.UTF_8))
        {
            stream.forEach(s -> {
              try
              {
                  var documentArray = s.split(";", 2);
                  documents.add(new Document(Long.parseLong(documentArray[0]), documentArray[1]));

              }
              catch (NumberFormatException e)
              {
                  e.printStackTrace();
              }
            });
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return documents;
    }
}
