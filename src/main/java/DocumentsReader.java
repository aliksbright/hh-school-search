import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DocumentsReader {

    private String path;
    private Map<Integer, Integer> offsetsMap = new HashMap<>();

    public DocumentsReader(String path) throws IOException {
        this.path = path;
        BufferedReader reader = new BufferedReader(new FileReader(path + "/docOffsets"));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] data = line.split(":");
            if (data.length != 2) {
                break;
            }
            int id = Integer.parseInt(data[0]);
            int offset = Integer.parseInt(data[1]);
            offsetsMap.put(id, offset);
        }
        reader.close();
    }

    public List<String> getDocuments(Collection<Integer> ids) throws IOException {
        List<Integer> offsets = ids.stream()
                .map(offsetsMap::get)
                .sorted()
                .collect(Collectors.toList());
        List<String> result = new ArrayList<>();
        Integer currentOffset = 0;
        BufferedReader reader = new BufferedReader(new FileReader(path + "/documents"));
        for (Integer offset : offsets) {
            reader.skip(offset - currentOffset);
            String document = reader.readLine();
            currentOffset = offset + document.length() + 1;
            result.add(document);
        }
        reader.close();
        return result;
    }
}