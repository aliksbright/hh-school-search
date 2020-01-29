import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;

public class DocumentsWriter implements Closeable {

    private Integer currentOffset;
    private BufferedWriter outputDocuments;
    private BufferedWriter docOffsetsFile;

    public DocumentsWriter(String path) throws IOException {
        this.currentOffset = 0;
        this.outputDocuments = new BufferedWriter(new FileWriter(path + "/documents"));
        this.docOffsetsFile = new BufferedWriter(new FileWriter(path + "/docOffsets"));
    }

    public void addDocument(Integer id, String document) throws IOException {
        outputDocuments.write(document);
        outputDocuments.newLine();
        docOffsetsFile.write(id.toString());
        docOffsetsFile.write(":");
        docOffsetsFile.write(currentOffset.toString());
        docOffsetsFile.newLine();
        currentOffset += document.length() + 1;
    }

    @Override
    public void close() throws IOException {
        outputDocuments.close();
        docOffsetsFile.close();
    }
}
