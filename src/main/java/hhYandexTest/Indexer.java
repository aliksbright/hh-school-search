package hhYandexTest;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiConsumer;


public class Indexer {
    static final String INDEX_FILE_NAME="index.bin";
    IndexHeader indexHeader;
    Path indexPath;
    File indexFile;

    public Indexer(String indexDirName) throws IndexerErrorException {

        indexPath = Path.of(indexDirName);
        if (!Files.isDirectory(indexPath))
            throw new IndexerErrorException("Index directory not exists");

        indexFile = new File(indexDirName, INDEX_FILE_NAME);

        if (indexFile.exists()) {
            FileInputStream fin;
            System.out.println("Reading index file header");

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

    void indexFile(String documentFile, BiConsumer<Long, String> inverseIndexer) throws IndexerErrorException {
        RandomAccessFile fout;
        BufferedReader fin ;


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
                int recordNumber = indexHeader.newRecord();
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
}


class IndexerErrorException extends Exception {
    String reason;

    public IndexerErrorException(String reason) {
        super(reason);
    }
}


class IndexHeader {
    /* Disk data below : */
    /* FIX header size and serislize/deserialize in case of any changes below */

    public static final int VERSION = 0x00_00_00_01;   // v0.0.0.1
    int     totalRecords;
    byte[]  recordStatus;
    /* FIX */

    public static final int MAX_RECORDS = 1024 * 1024; /* 2**20 records per file*/
    public static final int HEADER_SIZE = 4 + 4 + MAX_RECORDS / 8;

    int newRecord() {
        recordStatus[Integer.divideUnsigned(totalRecords, 8)] |= (1<<(totalRecords%8));
        totalRecords++;
        return totalRecords - 1;
    }

    void deleteRecord(int record) {
        if (record >= totalRecords)
            return;
        recordStatus[Integer.divideUnsigned(record,8)] &= ~(1<<(record%8));
    }

    public IndexHeader() {
        totalRecords = 0;
        recordStatus = new byte[IndexHeader.MAX_RECORDS / 8];
    }

    public IndexHeader(int totalRecords, byte[] recordStatus) {
        this.totalRecords = totalRecords;
        this.recordStatus = recordStatus;
    }


    byte [] serialize() {
        byte[] serial = new byte[HEADER_SIZE];
        int offset = 0;

        ByteBuffer.allocate(HEADER_SIZE)
            .order(ByteOrder.BIG_ENDIAN)
                .putInt(VERSION)
                .putInt(totalRecords)
                .put(recordStatus)
                .rewind()
                .get(serial);

        return serial;
    }

    void toFile(RandomAccessFile f) throws IOException {
        byte [] serialized = serialize();
        f.write(serialized);
    }


    public static IndexHeader fromFile(FileInputStream f) throws IOException {
        byte [] integer = new byte[4];
        byte [] recordStatus;
        int version = 0;
        int totalRecords = 0;

        f.readNBytes(integer, 0, integer.length);
        for (byte b : integer)
            version |= (version << 8) | b;

        if (version != VERSION)
            return null;

        f.readNBytes(integer, 0, integer.length);
        for (byte b : integer)
            totalRecords |= (totalRecords << 8) | b;

        recordStatus = new byte[MAX_RECORDS / 8];
        f.readNBytes(recordStatus, 0, recordStatus.length);

        return new IndexHeader(totalRecords, recordStatus);
    }

}



class IndexRecord {

    int recordSize;
    int recordNumber;
    long documentNumber;
    byte [] text;

    public IndexRecord(int recordNumber, long documentNumber, byte [] text) {
        this.recordNumber = recordNumber;
        this.documentNumber = documentNumber;
        this.text = text;
        this.recordSize = 4 + 4 + 8 + text.length;
    }

    public IndexRecord(int recordNumber, long documentNumber, String text) {
        this(recordNumber, documentNumber, text.getBytes(StandardCharsets.UTF_8));
    }


    byte [] serialize() {
        byte[] serial = new byte[recordSize];
        int offset = 0;

        ByteBuffer.allocate(recordSize)
                .order(ByteOrder.BIG_ENDIAN)
                .putInt(recordSize)
                .putInt(recordNumber)
                .putLong(documentNumber)
                .put(text)
                .rewind()
                .get(serial);

        return serial;
    }

    public static IndexRecord fromFile(FileInputStream f) throws IOException {
        byte [] integer = new byte[4];
        byte [] longInt = new byte[8];
        byte [] text;
        int recordSize = 0;
        int recordNumber = 0;
        long documentNumber = 0;

        f.readNBytes(integer, 0, integer.length);
        for (byte n : integer)
            recordSize |= (recordSize <<8) | n;

        f.readNBytes(integer, 0, integer.length);
        for (byte n : integer)
            recordNumber |= (recordNumber <<8) | n;

        f.readNBytes(longInt, 0, longInt.length);
        for (byte n : longInt)
            documentNumber |= (documentNumber <<8) | n;

        text = new byte[recordSize - 4 - 4 -8];
        f.readNBytes(text, 0, text.length);

        return new IndexRecord(recordNumber, documentNumber, text);
    }

    void toFile(RandomAccessFile f) throws IOException {
        byte [] serialized = serialize();
        f.write(serialized);
    }

}
