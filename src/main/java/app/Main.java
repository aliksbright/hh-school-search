package app;

import app.index.Indexing;
import app.search.Searching;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Write work mode: \"index\" or \"search\"\nAlso can use: \"i\" or \"s\"");
            String mode = scanner.nextLine();
            if (mode.equals("index") || mode.equals("i")) {
                Indexing.runIndexing();
                break;
            } else if (mode.equals("search") || mode.equals("s")) {
                Searching.runSearching();
                break;
            } else {
                System.out.println("Incorrect work mode");
            }
        }
    }
}
