package hhYandexTest.InverseIndexer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class TermManager {
    final int   JUMP_STEP       = 100;
    final int   DOC_LIST_GROW   = 50;

    TreeMap<Long, Integer> jumpTable;       // Pointers to docList
    byte [] docList;                        // TODO: Array size is less than 2**32-1, so
    int     docListSize;                    // TODO: may require to switch to ArrayList

    long    totalDocs;
    /*
        Default constructor
     */
    public TermManager()  {
        jumpTable = new TreeMap<>();
        docList = new byte[DOC_LIST_GROW];
        docListSize = 0;
        totalDocs = 0;
    }


    /*
        Term constructor with initial data previously obtained with getRawData
     */
    public TermManager(byte [] rawTermData) {
        this();

        int offset   = 0;
        int numSegments = 0;

        for (int n=0;n<4;n++, offset++) {
            numSegments <<= 8;
            numSegments |= (rawTermData[offset] & 0xFF);
        }

        jumpTable = new TreeMap<>();

        for (int segIdx = 0; segIdx<numSegments; segIdx++) {
            long    segment     = 0;
            int     jumpOffset  = 0;

            for (int n=0;n<8; n++, offset++) {
                segment <<= 8;
                segment |= (rawTermData[offset] & 0xFF);
            }

            for (int n=0;n<4; n++, offset++) {
                jumpOffset <<= 8;
                jumpOffset |= (rawTermData[offset] & 0xFF);
            }

            jumpTable.put(segment, jumpOffset);
        }

        docList = Arrays.copyOfRange(rawTermData, offset, rawTermData.length);
        docListSize = docList.length;
    }


    /*
        TODO: It's better to write directly to file
     */
    public byte [] getRawData() {

        Set<Long> segments    = jumpTable.keySet();
        int jumpTableSize       = 4 + (8 + 4) * segments.size();
        int resultLength        = jumpTableSize + docListSize;
        byte [] result          = new byte[resultLength];

        ByteBuffer resultBuffer = ByteBuffer
                .allocate(resultLength)
                .order(ByteOrder.BIG_ENDIAN)
                .putInt(segments.size());

        Map.Entry<Long, Integer> iterator = jumpTable.firstEntry();

        while (iterator != null) {
            resultBuffer
                    .putLong(iterator.getKey())
                    .putInt(iterator.getValue());
            iterator = jumpTable.higherEntry(iterator.getKey());
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
        totalDocs++;

    }

    public DocInfo getDocInfo(int startPosition) {
        int  position = startPosition;
        long doc = 0;

        while (position < docListSize) {
            doc <<=7;
            doc |= docList[position] & 0x7F;

            if ((docList[position] & 0x80) == 0)
                break;
            position++;
        }

        if (position >= docListSize)
            return  null;

        position++;

        //
        // TODO: Here we should extract metadata correctly now it's just a stub
        byte sz = docList[position];
        int  termPosition;
        if (sz == 1)
            termPosition = docList[position+1];
        else
            termPosition = 0;

        return new DocInfo(doc, termPosition, position - startPosition + 1 + sz, totalDocs);
    }

    public void add(byte [] document) {
        add(document, null);
    }

}

