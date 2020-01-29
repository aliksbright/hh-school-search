package hh.school.additional;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Documents {
    private String savePath;
    private Integer maxId = -1;
    private BufferedWriter writer;
    private Map<Integer, String> documentsMap;

    public Documents(String savePath, Boolean indexing) {
        try {
            this.savePath = savePath + "/documents.txt";
            File docs = new File(this.savePath);
            if (docs.createNewFile()) {
                maxId = 0;
            } else {
                BufferedReader reader = new BufferedReader(new FileReader(this.savePath));
                String line;
                documentsMap = new HashMap<>();
                while((line = reader.readLine()) != null) {
                    String[] splittedString = line.split("@");
                    documentsMap.put(Integer.parseInt(splittedString[0]), splittedString[1]);
                    maxId = Math.max(maxId, Integer.parseInt(splittedString[0]));
                }
                reader.close();
            }
            if (indexing) writer = new BufferedWriter(new FileWriter(this.savePath, true));
        } catch (Exception e) {
            System.out.println("System exception " + e.getMessage());
        }

    }

    public Integer addLineInDocuments(String line) throws IOException {
        maxId++;
        writer.write(maxId + "@" + line + "\n");
        return maxId;
    }

    public void close() throws IOException {
        writer.close();
    }

    public String getDocsById(Integer docId) {
        if (documentsMap.containsKey(docId)) return documentsMap.get(docId);
        else return "";
    }

}
