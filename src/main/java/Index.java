import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Index {

    private Map<String, ArrayList<Integer>> revertIndex = new HashMap<>();

    public Map<String, ArrayList<Integer>> getRevertIndex() {
        return revertIndex;
    }

    public Index(Path path) {
        loadIndex(path);
    }

    public Index() {
    }

    public void addPair(String word, Integer docId) {
        if (revertIndex.containsKey(word)) {
            ArrayList<Integer> list = revertIndex.get(word);
            if (!list.contains(docId))
                revertIndex.get(word).add(docId);
        } else {
            ArrayList<Integer> list = new ArrayList<>();
            list.add(docId);
            revertIndex.put(word, list);
        }
    }

    public void saveIndex(Path path) {
        try {
            Files.writeString(path, new Gson().toJson(revertIndex));
            System.out.println("Index file was created here: " + path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadIndex(Path path) {
        try {
            String json = Files.readString(path);
            Type indexType = new TypeToken<HashMap<String, ArrayList<Integer>>>(){}.getType();
            revertIndex = new Gson().fromJson(json, indexType);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

