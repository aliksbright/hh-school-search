
public class Main {
    public static void main(String[] args) {
        switch (args[0]) {
            case "INDEX":
                index(args[1], args[2]);
                break;
            case "SEARCH":
                search(args[1], args[2]);
                break;
            default:
                System.out.println("There's not such mode. Try again.");
                break;
        }
    }

    private static void index(String pathToIndex, String pathToSourceFile) {
        System.out.println("YOU ARE IN INDEX MODE");
        System.out.println("param1: " + pathToIndex);
        System.out.println("param2: " + pathToSourceFile);
    }

    private static void search(String pathToIndex, String searchQuery) {
        System.out.println("YOU ARE IN SEARCH MODE");
        System.out.println("param1: " + pathToIndex);
        System.out.println("param2: " + searchQuery);
    }
}
