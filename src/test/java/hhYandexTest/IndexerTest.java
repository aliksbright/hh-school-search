package hhYandexTest;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public class IndexerTest {
    static final String TEST_INDEX = "test-index";
    static final String TEST_FILE = "test-file.txt";
    static final String TEST_SHORT_FILE = "test-short-file.txt";

    @Test
    public void indexHeaderTest() {
        RandomAccessFile f;

        try {
            f = new RandomAccessFile(TEST_INDEX+"/index.bin", "rw");
        } catch (Exception e) {
            System.out.println(e);
            return;
        }

        IndexHeader header = new IndexHeader();
        try {
            header.toFile(f);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    void indexerTestPrepare() {
        Path indexDir = Path.of(TEST_INDEX);

        try {
            Files.walk(indexDir).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        } catch (Exception e) {
            // pass
        }

        indexDir.toFile().mkdir();
    }

    @Test
    public void indexerTest() throws IndexerErrorException {
        indexerTestPrepare();

        Indexer idx  = new Indexer(TEST_INDEX);
        idx.indexFile(TEST_FILE, null);
    }

    @Test
    public void inverseIndexTest() throws IndexerErrorException, IOException {
        indexerTestPrepare();

        InverseIndex    inverseIndex = new InverseIndex(TEST_INDEX);

        inverseIndex.accept(0L, "   Привет,    дорогой друг !");
        inverseIndex.accept(1L, "Верной дорогой идем, товарищи!");
        inverseIndex.dump();
        inverseIndex.save();
    }

    @Test
    public void indexerWithInverseIndexerTest() throws IndexerErrorException, IOException {
        indexerTestPrepare();

        Indexer         idx = new Indexer(TEST_INDEX);
        InverseIndex    inverseIndex = new InverseIndex(TEST_INDEX);

        idx.indexFile(TEST_FILE, inverseIndex);
        inverseIndex.dump();
        inverseIndex.save();
    }

    /*
        0xFF FF FF =>       1111 1111 1111 1111 1111 1111
                        '--87-''-- FF--''- FF --''- 7F -'
     */
    @Test
    public void varByteTest() {
        byte [] x;

        x = VarByte.longToVarByte(0xFFFFFF);
        assertArrayEquals(x, new byte [] {(byte) 0x87, (byte) 0xFF, (byte) 0xFF, 0x7F});

        x = VarByte.longToVarByte(0xFFFFFFFF_FFFFFFFFL);
        assertArrayEquals(
            x,
            new byte [] {
                    (byte) 0x81, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                    (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x7F
            }
        );

        x = VarByte.longToVarByte(0x3FFF);
        assertArrayEquals(x, new byte [] {(byte) 0xFF, (byte) 0x7F});

        StringBuilder   b = new StringBuilder();

        for (byte n : x)
            b.append(String.format("%X ", n));
        System.out.println("["+b.toString()+"]");
    }





    @Test
    public void termManagerTest() {
        TermManager manager = new TermManager();

        manager.add(new byte[] {1});
        manager.add(new byte[] {50}, "Some data".getBytes(StandardCharsets.UTF_8));
        manager.add(new byte[] {100});
        manager.add(new byte[] {(byte)0x8F, 1},"12738176248716381763131313131231391873".getBytes(StandardCharsets.UTF_8));
        manager.add(new byte[] {25}, "Very long metadata to grow".getBytes(StandardCharsets.UTF_8));
        manager.add(new byte[] {(byte)0x80, 1}, "One more 1".getBytes(StandardCharsets.UTF_8));
        manager.dump();
    }

}

