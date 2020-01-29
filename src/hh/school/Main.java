package hh.school;

import hh.school.index.Indexing;
import hh.school.search.Searching;

public class Main {

    public static void main(String[] args) {
	    try {
            switch (args[0]) {
                case "-i":
                case "--index":
                    new Indexing(args[1], args[2]);
                    System.out.println("Success indexing.");
                    break;
                case "-s":
                case "--search":
                    new Searching(args[1], args[2]);
                    break;
                default:
                    System.out.println("Operations " + args[0] + " not supported.");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
	        System.out.println("Missing operand.");
        }

    }
}
