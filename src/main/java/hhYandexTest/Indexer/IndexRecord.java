package hhYandexTest.Indexer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

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

    public static IndexRecord fromFile(RandomAccessFile f) throws IOException {
        byte [] integer = new byte[4];
        byte [] longInt = new byte[8];
        byte [] text;
        int recordSize = 0;
        int recordNumber = 0;
        long documentNumber = 0;

        f.read(integer);
        for (byte n : integer)
            recordSize |= (recordSize <<8) | (n & 0xFF);

        f.read(integer);
        for (byte n : integer)
            recordNumber |= (recordNumber <<8) | (n & 0xFF);

        f.read(longInt);
        for (byte n : longInt)
            documentNumber |= (documentNumber <<8) | (n & 0xFF);

        text = new byte[recordSize - 4 - 4 -8];
        f.read(text);

        return new IndexRecord(recordNumber, documentNumber, text);
    }

    void toFile(RandomAccessFile f) throws IOException {
        byte [] serialized = serialize();
        f.write(serialized);
    }

}
