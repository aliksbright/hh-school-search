package homework.search.service;

import homework.search.object.Document;
import homework.search.object.TermPosition;

import java.io.*;
import java.util.*;

public class IndexBuilder {

    private static File fileIndex;

    public IndexBuilder(String filePath) {
        fileIndex = new File(filePath);
    }

    public static void writeIndex(List<Document> documents) {
        createDir();
        HashMap<String, List<TermPosition>> index = getInvertIndexFromDocs(documents);
        try (FileWriter fWr = new FileWriter(fileIndex, false)) { //false перезапись файла
            for (Map.Entry <String, List<TermPosition>> entry : index.entrySet()){
                StringJoiner strJoin = new StringJoiner(";");
                entry.getValue().forEach(numDoc -> strJoin.add(String.valueOf(numDoc)));
                fWr.write( entry.getKey() + ":" + strJoin + "\n");
            }
            fWr.flush();
            System.out.println("Файл индекса создан!");

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static HashMap<String, List<TermPosition>> readIndex() {
        HashMap<String, List<TermPosition>> index = new HashMap<>();
        try (FileReader fr = new FileReader(fileIndex)){
            BufferedReader br = new BufferedReader(fr);
            String line;
            while((line = br.readLine()) != null){
                String[] wordsLine = line.split("[:;]");
                List <TermPosition> setDoc = new ArrayList<>();
                for (int i=1; i<wordsLine.length; i++){
                    String[] docIdTerms = wordsLine[i].split("[,\\[\\]\\s]+");
                    List<Integer> termPos = new ArrayList<>();
                    for (int j=1; j<docIdTerms.length; j++){
                        termPos.add(Integer.valueOf(docIdTerms[j]));
                    }
                    setDoc.add(new TermPosition(Integer.valueOf(docIdTerms[0]), termPos));
                }
                index.put(wordsLine[0],setDoc);
            }
        }
        catch (IOException ex){
            System.out.println(ex.getMessage());
        }
        return index;
    }

    public static HashMap<String, List<TermPosition>> getInvertIndexFromDocs(List<Document> documents){
        HashMap<String, List<TermPosition>> invertIndex = new HashMap<>();
        for (Document doc:documents){
            buildInvertIndex(invertIndex, doc);
        }
        return invertIndex;
    }

    public static void buildInvertIndex(HashMap<String, List<TermPosition>> invertIndex, Document doc) {
        for (Map.Entry <String, List<Integer>> entry : getTermsFromDoc(doc).entrySet()){
            if (!invertIndex.containsKey(entry.getKey())){
                invertIndex.put(entry.getKey(), new ArrayList<>());
            }
            invertIndex.get(entry.getKey()).add(new TermPosition(doc.getDocId(),entry.getValue()));
        }
    }

    public static Map<String, List<Integer>> getTermsFromDoc(Document doc) {
        Map<String, List<Integer>> termPositionInDoc = new HashMap<>();
        for (int idx=0; idx<doc.getTerms().size(); idx++){
            if (!termPositionInDoc.containsKey(doc.getTerms().get(idx))){
                termPositionInDoc.put(doc.getTerms().get(idx), new ArrayList<>());
            }
            termPositionInDoc.get(doc.getTerms().get(idx)).add(idx);
        }
        return termPositionInDoc;
    }
    //создаем директорию для индекса, если ее не существует
    private static void createDir() {
        if (fileIndex.exists())
            return;
        if (fileIndex.getParentFile()!=null){
            if(!fileIndex.getParentFile().mkdir()) {
                System.out.println("Файл индекса не создан!");
            }
        }
    }
}

