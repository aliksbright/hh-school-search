package hhYandexTest.Indexer;

import hhYandexTest.IndexerErrorException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiConsumer;


public class Index {
    static final String INDEX_FILE_NAME="index.bin";
    IndexHeader indexHeader;
    Path        indexPath;
    File        indexFile;


    public Index(String indexDirName) throws IndexerErrorException {

        indexPath = Path.of(indexDirName);
        if (!Files.isDirectory(indexPath))
            throw new IndexerErrorException("Index directory not exists");

        indexFile = new File(indexDirName, INDEX_FILE_NAME);

        if (indexFile.exists()) {
            FileInputStream fin;

            try {
                fin = new FileInputStream(indexFile);
                indexHeader = IndexHeader.fromFile(fin);
            } catch (Exception e) {
                throw new IndexerErrorException("Can't read index file");
            }

        } else {
            RandomAccessFile fout;

            System.out.println("Creating new empty index");
            try {
                fout = new RandomAccessFile(indexFile, "rw");
            } catch (Exception e) {
                throw new IndexerErrorException("Can't create Index File");
            }
            indexHeader = new IndexHeader();

            try {
                indexHeader.toFile(fout);
            } catch (Exception e) {
                throw new IndexerErrorException("Can't write to index file");
            }

            try {
                fout.close();
            } catch (Exception e) {
                // Pass
            }
        }
    }

    public void indexFile(String documentFile, BiConsumer<Long, String> inverseIndexer) throws IndexerErrorException {
        RandomAccessFile    fout;
        BufferedReader      fin ;


        try {
            fin = new BufferedReader(
                new InputStreamReader(
                    new FileInputStream(documentFile),
                    StandardCharsets.UTF_8
                )
            );
        } catch (Exception e) {
            throw new IndexerErrorException("Input file not found");
        }

        try {
            fout = new RandomAccessFile(indexFile, "rw");
            fout.seek(fout.length());
        } catch (Exception e) {
            throw new IndexerErrorException("Can't append to Index File");
        }

        try {
            String doc;
            while ((doc = fin.readLine()) != null) {
                if (indexHeader.totalRecords >= IndexHeader.MAX_RECORDS) {
                    // TODO: Added sharding support
                    throw new IndexerErrorException("Index file overflow. Please expand.");
                }
                int recordNumber;
                try {
                    recordNumber =  indexHeader.newRecord();
                } catch (Exception e) {
                    // TODO: Add volume support
                    System.out.println("Too many documents, some documents were ignored");
                    break;
                }
                long documentNumber = recordNumber;

                // TODO: Add some real document number
                IndexRecord record = new IndexRecord(recordNumber, documentNumber, doc);
                record.toFile(fout);
                if (inverseIndexer != null)
                    inverseIndexer.accept(documentNumber, doc);
            }
        } catch (Exception e) {
            throw new IndexerErrorException("Can't read source file");
        }

        try {
            fout.seek(0);
            indexHeader.toFile(fout);
        } catch (Exception e) {
            throw new IndexerErrorException("Can't rewrite header");
        }
    }


    public long totalDocuments() {
        return indexHeader.totalRecords;

    }

    public String find(long docId) throws IOException {
        IndexRecord         r = null;
        RandomAccessFile    searchFile = null;

        searchFile = new RandomAccessFile(indexFile, "r");
        searchFile.seek(IndexHeader.HEADER_SIZE);

        while (searchFile.getFilePointer() < searchFile.length()) {
            try {
                r = IndexRecord.fromFile(searchFile);
            } catch (Exception e) {
                searchFile.close();
                return null;
            }

            if (indexHeader.isAltered(r.recordNumber))
                continue;

            if (r.documentNumber == docId)
                break;
        }

        searchFile.close();
        if (r == null)
            return null;

        return new String(r.text, StandardCharsets.UTF_8);
    }

}


