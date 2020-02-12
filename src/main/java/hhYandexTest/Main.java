package hhYandexTest;

import hhYandexTest.Indexer.Index;
import hhYandexTest.InverseIndexer.DocInfo;
import hhYandexTest.InverseIndexer.InverseIndex;
import hhYandexTest.Searcher.SearchRequest;

import java.io.File;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;


public class Main {

    static void printUsage() {
        System.out.println("Index mode : ");
        System.out.println("   <app> -i <IndexDirectory> <DocumentsFile>");
        System.out.println("Search mode : ");
        System.out.println("   <app> -s <IndexDirectory> <SearchRequest>");
        System.out.println("   <app> -s <IndexDirectory> <SearchRequest>");
    }

    static void buildIndex(String indexDirectory, String docFile) {
        File indexDir = new File(indexDirectory);

        if (indexDir.exists()) {
            if (indexDir.isDirectory() == false) {
                System.out.println("First parameter must be directory name");
                printUsage();
                return;
            }
        } else
            indexDir.mkdir();

        try {
            Index idx = new Index(indexDirectory);
            InverseIndex inverseIndex = new InverseIndex(indexDirectory);
            idx.indexFile(docFile, inverseIndex);
            inverseIndex.save();
            System.out.println("Index and reverse index created !");
            System.out.println("Index size          : "+idx.totalDocuments());
            System.out.println("Total known terms   : "+inverseIndex.totalTerms());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void search(String indexDirectory, String query) throws Exception {
        Index idx = new Index(indexDirectory);
        InverseIndex inverseIndex = new InverseIndex(indexDirectory);

        SearchRequest  req = new SearchRequest(query);

        req.setInverseIndex(inverseIndex);
        req.setTotalDocsuments(idx.totalDocuments());
        DocInfo doc = req.pull();

        while (doc != null) {
            String docText = idx.find(doc.docId);
            if (docText != null)
                System.out.println(docText);
            doc = req.pull();
        }
    }


    public static void main(String... args) throws Exception {
        String indexDirectory;

        if (args.length < 3) {
            printUsage();
            return;
        }

        if (args[0].equals("-i"))
            buildIndex(args[1], args[2]);
        else if (args[0].equals("-s")) {
            search(args[1], args[2]);
        } else {
            System.out.println("Incorrect command " + args[0]);
            printUsage();
            return;
        }


    }
}
