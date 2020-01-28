package hhYandexTest.Indexer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class IndexHeader {
    /* Disk data below : */
    /* FIX header size and serislize/deserialize in case of any changes below */

    public static final int VERSION = 0x00_00_00_01;   // v0.0.0.1
    public int     totalRecords;
    byte[]  recordStatus;
    /* FIX */

    public static final int MAX_RECORDS = 1024 * 1024; /* 2**20 records per file*/
    public static final int HEADER_SIZE = 4 + 4 + MAX_RECORDS / 8;

    public int newRecord() {
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

    public void toFile(RandomAccessFile f) throws IOException {
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
