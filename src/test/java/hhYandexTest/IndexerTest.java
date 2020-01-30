package hhYandexTest;

import hhYandexTest.Indexer.IndexHeader;
import hhYandexTest.Indexer.Index;
import hhYandexTest.InverseIndexer.DocInfo;
import hhYandexTest.InverseIndexer.InverseIndex;
import hhYandexTest.InverseIndexer.TermManager;
import hhYandexTest.Searcher.SearchRequest;
import org.junit.Test;
import static org.junit.Assert.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

public class IndexerTest {
    static final String TEST_INDEX = "test-index";
    static final String TEST_FILE = "test-file.txt";
    static final String TEST_SHORT_FILE = "test-short-file.txt";
    static final String TEST_HELLO_WORLD_FILE = "test-hello-world.txt";
    static final String TEST_HELLO_NEAR_FILE = "hello-near-test.txt";

    @Test
    public void indexHeaderTest() {
        RandomAccessFile f;

        try {
            f = new RandomAccessFile(TEST_INDEX+"/index.bin", "rw");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        IndexHeader header = new IndexHeader();
        try {
            header.toFile(f);
        } catch (Exception e) {
            e.printStackTrace();
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

        Index idx  = new Index(TEST_INDEX);
        idx.indexFile(TEST_FILE, null);
    }

    @Test
    public void inverseIndexTest() throws IndexerErrorException, IOException {
        indexerTestPrepare();

        InverseIndex inverseIndex = new InverseIndex(TEST_INDEX);

        inverseIndex.accept(0L, "   Привет,    дорогой друг !");
        inverseIndex.accept(1L, "Верной дорогой идем, товарищи!");
        inverseIndex.dump();
        inverseIndex.save();

        System.out.println("---------------");

        assertEquals(inverseIndex, new InverseIndex(TEST_INDEX, true));
        assertNotEquals(inverseIndex, new InverseIndex(TEST_INDEX, false));

    }

    @Test
    public void indexerWithInverseIndexerTest() throws IndexerErrorException, IOException {
        indexerTestPrepare();

        Index           idx             = new Index(TEST_INDEX);
        InverseIndex    inverseIndex    = new InverseIndex(TEST_INDEX);

        idx.indexFile(TEST_FILE, inverseIndex);
        System.out.println("Total documents :"+idx.totalDocuments());
        inverseIndex.save();

        System.out.println("Reading again ...");
        Index           idx1             = new Index(TEST_INDEX);
        InverseIndex    inverseIndex1    = new InverseIndex(TEST_INDEX);


        System.out.println("Loaded documents :"+idx1.totalDocuments());

        assertEquals(inverseIndex, inverseIndex1);

        System.out.println("Indexing again");
        idx1.indexFile(TEST_FILE, inverseIndex1);

        System.out.println("Saving inverse index");
        inverseIndex1.save();
        System.out.println("Done");

        System.out.println("Total documents :"+idx1.totalDocuments());
    }

    /*
        0xFF FF FF =>  0000 1111 1111 1111 1111 1111 1111
                       '---87-''-- FF--''- FF --''- 7F -'
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

    @Test
    public void requestBuilderTest() throws Exception {
        SearchRequest req;

        req = new SearchRequest("hello");
        req.test();

        req = new SearchRequest("(NOT abc) OR (world of fire)");
        req.test();
    }

    @Test
    public void requestBuilderWithInverseIndex() throws Exception {
        indexerTestPrepare();

        Index           idx             = new Index(TEST_INDEX);
        InverseIndex    inverseIndex    = new InverseIndex(TEST_INDEX);

        idx.indexFile(TEST_HELLO_WORLD_FILE, inverseIndex);

        SearchRequest       req             = new SearchRequest("(daddy OR hello) AND (say OR when)");
        Iterator<Integer>   answer          = List.of(2,3).stream().iterator();

        req.setInverseIndex(inverseIndex);
        DocInfo doc = req.pull();

        while (doc != null) {
            assertTrue(answer.hasNext());
            Integer next = answer.next();

            System.out.println(doc.docId + " vs " + next);
            assertTrue(doc.docId == next);

            doc = req.pull();
        }

        assertFalse(answer.hasNext());
    }

    @Test
    public void requestBuilderWithNoAndIndex() throws Exception {
        indexerTestPrepare();

        Index           idx             = new Index(TEST_INDEX);
        InverseIndex    inverseIndex    = new InverseIndex(TEST_INDEX);

        idx.indexFile(TEST_HELLO_NEAR_FILE, inverseIndex);

        SearchRequest       req             = new SearchRequest("dear friend");
        req.setInverseIndex(inverseIndex);
        req.setTotalDocsuments(idx.totalDocuments());
        DocInfo doc = req.pull();

        Iterator<Integer>   answer          = List.of(1,2).stream().iterator();

        while (doc != null) {
            assertTrue(answer.hasNext());
            System.out.println(doc.docId);
            assertTrue(doc.docId == answer.next());
            doc = req.pull();
        }
        assertFalse(answer.hasNext());

        req             = new SearchRequest("friend dear");
        req.setInverseIndex(inverseIndex);
        req.setTotalDocsuments(idx.totalDocuments());
        doc = req.pull();

        assertNull(doc);
    }

    @Test
    public void requestBuilderWithInverseIndexAndDocumentOutput() throws Exception {
        indexerTestPrepare();

        Index           idx             = new Index(TEST_INDEX);
        InverseIndex    inverseIndex    = new InverseIndex(TEST_INDEX);

        idx.indexFile(TEST_HELLO_WORLD_FILE, inverseIndex);

        SearchRequest       req         = new SearchRequest("(daddy OR world)");
        Iterator<String>    answer      = List.of(
                "0 hello, dear world",
                "2 say hello to daddy",
                "3 when the world says hello to you",
                "4 welcome to the real world") .stream().iterator();
        req.setInverseIndex(inverseIndex);
        DocInfo doc = req.pull();

        while (doc != null) {
            String docText = idx.find(doc.docId);

            assertNotNull(docText);
            assertTrue(answer.hasNext());

            System.out.println(doc.docId + " " + docText);
            assertEquals(docText, answer.next());
            doc = req.pull();
        }

        assertFalse(answer.hasNext());
    }

}

