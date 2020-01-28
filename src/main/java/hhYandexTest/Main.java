package hhYandexTest;

import hhYandexTest.Indexer.Index;
import hhYandexTest.InverseIndexer.InverseIndex;

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

            if ( java.nio.file.Files.isDirectory(Paths.get(indexDirectory)) == false) {
                System.out.println("Directory " + indexDirectory + " not found");
                printUsage();
                return;
            }

            try {
                Index idx = new Index(indexDirectory);
                InverseIndex inverseIndex = new InverseIndex(indexDirectory);
                idx.indexFile(docFile, inverseIndex);
                inverseIndex.dump();
                inverseIndex.save();
                System.out.println("Index and reverse index created !");
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
