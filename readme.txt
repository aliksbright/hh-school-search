hhYandex indexer
~~~~~~~~~~~~~~~~

Index some file and put results to specified directory:
    java -jar hh-yandex-1.0-SNAPSHOT.jar -i <index directory> <path to file>


Type definitions
~~~~~~~~~~~~~~~~
uInt    - unsigned 32bit value
Long    - unsigned 64Bit value
Byte order on disk is BIG-ENDIAN (Network byte order)

Index file (index.bin)
~~~~~~~~~~~~~~~~~~~~~~

Index file contains header and a set of records.
Header structure:
-------------------------------------------------------------------
Field         Size     Type        Description
-------------------------------------------------------------------
version        4b      uInt        File Version
totalRecords   4b      uInt        Total records in file
status         1MB     byte[1M]    Bit string of records statuses
                                   Total 8 * 1024 * 1024 records
                                   per file
                                   * 1 - actual
                                   * 0 - obsolete
-------------------------------------------------------------------

Index record structure:
-----------------------------------------------------------------
Field           Size     Type    Description
-----------------------------------------------------------------
recordSize      4b       uInt    File size, including recordSize
recordNumber    4b       uInt    Record number inside file
documentNumber  8b       Long    Global document number
text            *        byte[]  Document body
-----------------------------------------------------------------
* text size = recordSize - recordNumber - ducomentNumber;


Inverse index (inverse.bin)
~~~~~~~~~~~~~~~~~~~~~~~~~~~

Inverse Index file is a set of following records:

------------------------------------------------------------------------------------
Field           Size         Type    Description
------------------------------------------------------------------------------------
termRecordSize  4b           uInt    Term record size including termRecordSize field
termNameSize    4b           uInt    Term name size
termName        termNameSize byte[]  Term name
JumpTable:
   jumpSize     4b           uInt    Jump table size
   [            jumpSize
    jumpSegment 8b           Long    Document segment
    offset      4b           uInt    Offset of this document in document list
   ]
Document list:  *
   docId        **           varByte Document number
   docMetaLen   1b           byte    Document metadata length
   docMeta      docDataLen   byte[]  Document metadata
------------------------------------------------------------------------------------

* documents.length = termRecordSize - 4 - 4 - termNameSize;
** docId is var-byte encoded value of document number;

