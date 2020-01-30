import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class IndexBuilder {

    private Path indexDir;
    private Path filePath;
    private Index index;

    public IndexBuilder(Path indexDir, Path filePath) {
        this.indexDir = Paths.get(indexDir + "/index.json");
        this.filePath = filePath;
        this.index = new Index();
    }

    public void buildIndex() {
        try {
            Integer docId = 0;
            List<String> lines = Files.readAllLines(filePath);
            for (String line : lines) {
                // Normalize text
                String text = line;
                text = TextNormalizer.cleanOutPunct(text);
                text = TextNormalizer.toLowerCase(text);
                Set<String> words = new HashSet<>(Arrays.asList(TextNormalizer.split(text)));

                // Lemmatize

                // Fill in index
                for (String word : words) {
                    index.addPair(word, docId);
                }
                docId++;
            }
            index.saveIndex(indexDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
