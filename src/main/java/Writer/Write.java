package Writer;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Write {
    public Write(Path indexPath, LinkedHashMap<String, ArrayList<String>> indexFile) throws Exception {
        this.indexWriter(indexPath, indexFile);
    }

    private void indexWriter(Path indexPath, LinkedHashMap<String, ArrayList<String>> indexFile) throws Exception {
        StringBuilder str = new StringBuilder();
        try (PrintWriter writer = new PrintWriter(indexPath.toFile())){
           for (Map.Entry<String, ArrayList<String>> map : indexFile.entrySet()) {
               str.append(map.getKey());
               for (String substr : map.getValue()){
                   str.append(">").append(substr);
               }
               writer.println(str);
               str.delete(0, str.length());
           }
       }
    }
}
