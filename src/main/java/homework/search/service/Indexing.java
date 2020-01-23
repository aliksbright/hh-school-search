package homework.search.service;

import java.io.*;
import java.util.*;

import static java.lang.Integer.parseInt;

public class Indexing {
    private static StopWordsAnalysis stopWordsAnalysis;
    private static Tokenizer tokenizer;
    private static File fileIndex;

    public Indexing(String filePath) {
        fileIndex = new File(filePath);
        stopWordsAnalysis = new StopWordsAnalysis();
        tokenizer = new Tokenizer();
    }

    public static HashMap<String, SortedSet<Integer>> getInvertIndex(List<String> inpDoc){
        HashMap<String, SortedSet<Integer>> invertIndex = new HashMap<>();
        int docId = 0;
        for (String doc:inpDoc){
            List<String> words = stopWordsAnalysis.execute(tokenizer.execute(doc));
            invertIndexForDoc(invertIndex, ++docId, words);
        }
        return invertIndex;
    }

    public static void invertIndexForDoc(HashMap<String, SortedSet<Integer>> invertIndex, Integer i, List<String> words) {
        for (String word:words){
            if (invertIndex.containsKey(word)){
                invertIndex.get(word).add(i);
            }
            else{
                TreeSet<Integer> setDocuments = new TreeSet<>();
                setDocuments.add(i);
                invertIndex.put(word, setDocuments);
            }
        }
    }

    public static HashMap<String, List<Integer>> readIndex() {
        HashMap<String, List<Integer>> index = new HashMap<>();
        try (FileReader fr = new FileReader(fileIndex)){
            BufferedReader br = new BufferedReader(fr);
            String line;
            while((line = br.readLine()) != null){
                String[] wordsLine = line.split("[:;]");
                List <Integer> setDoc = new ArrayList<>();
                for (int i=1; i<wordsLine.length; i++){
                    setDoc.add(Integer.valueOf(wordsLine[i]));
                }
                index.put(wordsLine[0],setDoc);
            }
        }
        catch (IOException ex){
            System.out.println(ex.getMessage());
        }
        return index;
    }

    public static void writeIndexFile(HashMap<String, SortedSet<Integer>> index) {
        try (FileWriter fWr = new FileWriter(fileIndex, false)) { //false перезапись файла
            for (Map.Entry <String, SortedSet<Integer>> entry : index.entrySet()){
                StringJoiner strJoin = new StringJoiner(";");
                entry.getValue().forEach(numDoc -> strJoin.add(numDoc + ""));
                fWr.write( entry.getKey() + ":" + strJoin + "\n");
            }
            fWr.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
