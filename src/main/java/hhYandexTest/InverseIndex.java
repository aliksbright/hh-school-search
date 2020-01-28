package hhYandexTest;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class InverseIndex implements BiConsumer<Long, String> {
    final static String INVERSE_INDEX_FILE_NAME = "inverse.bin";

    Map<String, TermManager> termMap;
    String indexDir;

    public InverseIndex(String indexDir) {
        termMap = new HashMap<>();
        this.indexDir = indexDir;
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
        Set<String>     terms;

        f = new RandomAccessFile(new File(indexDir, INVERSE_INDEX_FILE_NAME),"rw");
        terms = termMap.keySet();

        for (String term : terms) {
            byte [] uIntBytes = {0, 0, 0, 0};
            byte [] termNameBytes = term.getBytes();
            long    start = f.getFilePointer();
            long    end;
            TermManager termManager = termMap.get(term);


            f.write(uIntBytes, 0, 4);  // Will update record size later

            ByteBuffer.allocate(4)
                    .order(ByteOrder.BIG_ENDIAN)
                    .putInt(termNameBytes.length)
                    .rewind()
                    .get(uIntBytes);
            f.write(uIntBytes, 0, 4);
            f.write(termNameBytes, 0, termNameBytes.length);

            byte [] termRawData = termManager.getRawData();
            f.write(termRawData, 0, termRawData.length);

            end = f.getFilePointer();
            f.seek(start);

            ByteBuffer.allocate(4)
                    .order(ByteOrder.BIG_ENDIAN)
                    .putInt((int) (end - start))
                    .rewind()
                    .get(uIntBytes);
            f.write(uIntBytes, 0, 4);  // Will update record size later
            f.seek(end);
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
        doc = document.replaceAll("[!~?#$%^&*_\\-=+'\"><()\\[\\]{}.,:;]", "");
        doc = doc.replaceAll("\\s+", " ");
        doc = doc.replaceAll("^\\s+|\\s+$", "");
        doc = doc.toLowerCase();

        String [] termList  = doc.split(" ");

        for (int n=0;n< termList.length;n++) {
            if (termList[n].length() < 3)
                return;
            addEntry(termList[n], docId, new byte [] {(byte)n});    /* Metadata now is just position */
        }
    }

}



class TermManager {
    final int   JUMP_STEP       = 100;
    final int   DOC_LIST_GROW   = 50;

    TreeMap<Long, Integer> jumpTable;       // Pointers to docList
    byte [] docList;                        // TODO: Array size is less than 2**32-1, so
    int     docListSize;                    // TODO: may require to switch to ArrayList

    public TermManager()  {
        jumpTable = new TreeMap<>();
        docList = new byte[DOC_LIST_GROW];
        docListSize = 0;
    }


    /*
        TODO: It's better to write directly to file
     */
    public byte [] getRawData() {

        Set<Long>   segments    = jumpTable.keySet();
        int jumpTableSize       = 4 + (8 + 4) * segments.toArray().length;
        int resultLength        = jumpTableSize + docListSize;
        byte [] result          = new byte[resultLength];

        ByteBuffer resultBuffer = ByteBuffer
            .allocate(resultLength)
            .order(ByteOrder.BIG_ENDIAN)
            .putInt(jumpTableSize);

        for (Long segment : segments) {
            resultBuffer
                .putLong(segment)
                .putInt(jumpTable.get(segment));
        }

        resultBuffer
            .put(docList, 0, docListSize)
            .rewind()
            .get(result);

        return result;
    }


    public void dump() {
        int  position;
        System.out.print("Jump Table " + jumpTable + " : documents [ ");

        position = 0;
        while (position < docListSize) {
            long doc = 0;

            while (position < docListSize) {
                doc <<= 7;
                doc |= docList[position] & 0x7F;

                if ((docList[position] & 0x80) == 0) {
                    System.out.print(doc + " ");
                    break;
                }
                position++;
            }

            position++;
            position += docList[position] + 1;
        }

        System.out.println("]");

    }

    public void add(byte [] document, byte [] metadata) {
        long docId;
        long docSegment;
        boolean needToUpdateDocList = false;
        int   position, growSize, startPos;
        boolean newSegment = false;

        docId = 0;
        for (byte n : document) {
            docId <<= 7;
            docId |= (n & 0x7F);
        }


        // Find beginning of our term segment
        docSegment = docId / JUMP_STEP;

        if (jumpTable.containsKey(docSegment))
            position = jumpTable.get(docSegment);
        else {
            Map.Entry<Long, Integer> prevKey = jumpTable.lowerEntry(docSegment);
            Map.Entry<Long, Integer> nextKey = jumpTable.higherEntry(docSegment);
            if (prevKey == null)
                position = 0;
            else
                position = prevKey.getValue();

            if (nextKey == null)            // Will skip segment position_search loop
                position = docListSize;

            newSegment = true;
        }

        // Now walk through segment while docIds there <= docId
        //System.out.println("Inserting " + docId + " in segment " + docSegment + " starting from " + position);

    position_seek:
        while (position < docListSize) {
            long doc = 0;
            startPos = position;

            while (position < docListSize) {
                doc <<=7;
                doc |= docList[position] & 0x7F;

                if ((docList[position] & 0x80) == 0) {
                    if (doc >= docId) {         // Allow multiple documents with same ids
                        position = startPos;
                        break position_seek;
                    }

                    //System.out.println("   * "+doc+" skipped");
                    break;
                }
                position++;
            }

            position++;
            position += docList[position] + 1;
        }

        // Add position to jumpTable, update all further positions
        if (newSegment)
            jumpTable.put(docSegment, position);

        growSize = document.length + (metadata == null ? 0 : metadata.length) + 1;

        // Update all further positions
        Map.Entry<Long, Integer> nextEntry;
        long    nextEntryKey = docSegment;
        while ((nextEntry = jumpTable.higherEntry(nextEntryKey))!= null) {
            //System.out.println("Updating trailing Segment " + nextEntry.getKey() + " on " + growSize);
            nextEntryKey = nextEntry.getKey();
            jumpTable.replace(nextEntry.getKey(), nextEntry.getValue() + growSize);
        }

        // Insert document int docList at position and grow

        if (docListSize+ growSize > docList.length)  {
            byte [] newList = new byte[docList.length + growSize + DOC_LIST_GROW];

            //System.out.println(" ! Grow docList because " + docListSize + "+" + growSize +">" + docList.length);

            System.arraycopy(docList, 0, newList, 0, position);
            System.arraycopy(
                docList, position, newList, position + growSize, docListSize - position
            );

            // Copy document and metadata
            System.arraycopy(document, 0, newList, position, document.length);
            if (metadata != null) {
                System.arraycopy(metadata, 0, newList, position + document.length + 1, metadata.length);
                newList[position + document.length] = (byte) metadata.length;
            } else
                newList[position + document.length] = 0;
            docList = newList;
        } else {
            //System.out.println(" * Shifting existing array on " + growSize);
            System.arraycopy(
                docList, position, docList, position + growSize, docListSize - position
            );

            System.arraycopy(document, 0, docList, position, document.length);
            if (metadata != null) {
                System.arraycopy(metadata, 0, docList, position + document.length + 1, metadata.length);
                docList[position + document.length] = (byte) metadata.length;
            } else
                docList[position + document.length] = 0;
        }

        //System.out.println(" * Document added : " + docId + " in segment " + docSegment + " at position " + position);
        docListSize += growSize;

    }

    public void add(byte [] document) {
        add(document, null);
    }

}
