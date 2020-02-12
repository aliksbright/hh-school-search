package hhYandexTest.InverseIndexer;

import hhYandexTest.IndexerErrorException;
import hhYandexTest.VarByte;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiConsumer;

public class InverseIndex implements BiConsumer<Long, String> {
    final static String INVERSE_INDEX_FILE_NAME = "inverse.bin";

    Map<String, TermManager> termMap;
    String indexDir;



    public InverseIndex(String indexDirName, boolean loadFromFile) throws IndexerErrorException, IOException {
        File    indexFile;
        this.indexDir = indexDirName;

        termMap = new HashMap<>();

        if (loadFromFile == false)
            return;

        if ( java.nio.file.Files.isDirectory(Paths.get(indexDirName)) == false)
            throw new IndexerErrorException("Index directory not exists");

        indexFile = new File(indexDirName, INVERSE_INDEX_FILE_NAME);

        if (indexFile.exists())
            load();
    }

    public InverseIndex(String indexDirName) throws IndexerErrorException, IOException {
        this(indexDirName, true);
    }

    public void dump() {
        termMap.forEach((term, termManager) -> {
           System.out.print(term + ": ");
           termManager.dump();
        });
    }


    /*
        See readme.txt for file structure
     */
    public void save() throws IOException {
        RandomAccessFile    f;
        Set<String>         terms;

        f = new RandomAccessFile(new File(indexDir, INVERSE_INDEX_FILE_NAME),"rw");
        terms = termMap.keySet();

        for (String term : terms) {
            byte [] uIntBytes       = {0, 0, 0, 0};
            byte [] termNameBytes   = term.getBytes();
            long    start           = f.getFilePointer();
            long    end;
            TermManager termManager = termMap.get(term);


            // Record size (will update later)
            f.write(uIntBytes);

            // TermName size
            ByteBuffer.allocate(4)
                    .order(ByteOrder.BIG_ENDIAN)
                    .putInt(termNameBytes.length)
                    .rewind()
                    .get(uIntBytes);
            f.write(uIntBytes);

            // TermName
            f.write(termNameBytes, 0, termNameBytes.length);

            // TermData (Jump table and docList)
            byte [] termRawData = termManager.getRawData();
            f.write(termRawData, 0, termRawData.length);

            // Update record size
            end = f.getFilePointer();
            f.seek(start);

            ByteBuffer.allocate(4)
                    .order(ByteOrder.BIG_ENDIAN)
                    .putInt((int) (end - start))
                    .rewind()
                    .get(uIntBytes);
            f.write(uIntBytes);
            f.seek(end);
        }

        f.close();
    }

    public void load() throws IOException {
        byte [] tempBuf = new byte[4];
        byte [] termBuf, termDataBuf;
        int termSize, termDataSize, recordSize;
        String termName;

        RandomAccessFile    f;
        Set<String>     terms;

        f = new RandomAccessFile(new File(indexDir, INVERSE_INDEX_FILE_NAME),"r");

        while (f.getFilePointer() < f.length()) {
            // Record Size
            f.read(tempBuf);
            recordSize = ByteBuffer.allocate(4)
                    .order(ByteOrder.BIG_ENDIAN)
                    .put(tempBuf)
                    .rewind()
                    .getInt();

            // Term Name size
            f.read(tempBuf);
            termSize = ByteBuffer.allocate(4)
                    .order(ByteOrder.BIG_ENDIAN)
                    .put(tempBuf)
                    .rewind()
                    .getInt();

            // Term
            termBuf = new byte[termSize];
            f.read(termBuf);
            termName = new String(termBuf, StandardCharsets.UTF_8);

            // Term Data
            termDataBuf = new byte[recordSize - 4 - 4 - termSize];
            f.read(termDataBuf);

            termMap.put(termName, new TermManager(termDataBuf));
        }

        f.close();
    }


    public void addEntry(String term, byte[] document, byte [] metadata) {
        TermManager termManager;

        if (termMap.containsKey(term) == false) {
            termManager = new TermManager();
            termMap.put(term, termManager);
        } else
            termManager = termMap.get(term);

        termManager.add(document, metadata);
    }


    public void accept(Long documentNumber, String document) {
        String doc;
        byte [] docId = VarByte.longToVarByte(documentNumber);

        // Cleanup
        doc = document.replaceAll("[~#$%^&*_=+'\"><()\\[\\]{}]", "");
        doc = document.replaceAll("[!?.,:;\\-]", " ");
        doc = doc.replaceAll("\\s+", " ");
        doc = doc.replaceAll("^\\s+|\\s+$", "");
        doc = doc.toLowerCase();

        String [] termList  = doc.split(" ");

        for (int n=0;n< termList.length;n++) {
            if (termList[n].length() < 3)
                continue;
            addEntry(termList[n], docId, new byte [] {(byte)n});    /* Metadata now is just position */
        }
    }

    public TermManager getTermManager(String term) {
        if (termMap.containsKey(term))
            return termMap.get(term);
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InverseIndex that = (InverseIndex) o;

        if (this.termMap.keySet().equals(that.termMap.keySet()) == false)
            return false;


        for (String term : termMap.keySet()) {
            TermManager otherManager, thisManager;

            otherManager = that.termMap.get(term);
            thisManager = this.termMap.get(term);

            byte [] thisRaw     = thisManager.getRawData();
            byte [] otherRaw    = otherManager.getRawData();

            if (Arrays.equals(thisRaw, otherRaw) == false)
                return false;
        }

        return true;
    }


    public int totalTerms() {
        return termMap.size();
    }
}
