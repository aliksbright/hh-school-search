package hhYandexTest;

import hhYandexTest.Indexer.Index;
import hhYandexTest.InverseIndexer.InverseIndex;

import java.io.File;
import java.nio.file.Paths;


public class Main {

    static void printUsage() {
        System.out.println("Index mode : ");
        System.out.println("   <app> -i <IndexDirectory> <DocumentsFile>");
        System.out.println("Search mode : ");
        System.out.println("   <app> -s <IndexDirectory> <SearchRequest>");
        System.out.println("   <app> -s <IndexDirectory> <SearchRequest>");
    }

    public static void main(String... args) {
        String indexDirectory;
        String argument;

        if (args.length < 3) {
            printUsage();
            return;
        }

        if (args[0].equals("-i")) {
            indexDirectory = args[1];
            String docFile = args[2];

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

        } else if (args[0].equals("-s")) {
            System.out.println("Under construction =)");
        } else {
            System.out.println("Incorrect command " + args[0]);
            printUsage();
            return;
        }


    }
}
