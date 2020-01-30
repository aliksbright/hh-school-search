import java.util.Arrays;
import java.util.stream.Collectors;

public class Arguments {

    public String[] arguments;
    public String regime;
    public String indexDir;
    public int searchRegime;
    public String query;
    public int N;

    public Arguments(String[] args){
        this.arguments = args;
        if (args.length < 3)
            throw new IllegalArgumentException("3 parameters expected: \n1.Regime(0/1)\n2.Index directory\n3.File directory with docs/Search query");

        this.regime = args[0].toLowerCase();
        if (!this.regime.equals("index")&!this.regime.equals("search"))
            throw new IllegalArgumentException("Unknown regime. Possible regimes are: index or search\n");

        this.indexDir = args[1];
    }

    public String getDocsPath() {
        return this.arguments[2];
    }

    public void prepareQuery(String[] args) {
        /*-Как определяем режим работы поиска:
        --Аргумента всего три - simpleSearch
        ---Аргументов больше трех и в них содержится and или not - boolSearch
        ---Аргументов больше трех и в них содержится - orSearch
        ---Аргументов больше трех и в них не содержится ни and, ни or, ни not - phraseSearch
        */
        if (args.length == 3) {
            this.searchRegime = 0;
            this.query = args[2].toLowerCase();
        }
        else {
            String lastArgs = Arrays.stream(args).skip(2).map(String::toLowerCase).collect(Collectors.joining(" "));
            int flagQueryWithLogic = Arrays.stream(args)
                    .skip(2)
                    .filter(word -> (word.equalsIgnoreCase("and"))|
                            (word.equalsIgnoreCase("or"))|
                            (word.equalsIgnoreCase("not")))
                    .collect(Collectors.joining(" "))
                    .length();
            if (flagQueryWithLogic == 0) {
                this.searchRegime = 2;
                this.query = lastArgs;
            }
            else {
                if (lastArgs.contains("and") | lastArgs.contains("not")) {
                    this.searchRegime = 1;
                    this.query = lastArgs;
                }
                else if (lastArgs.contains("or")) {
                    this.searchRegime = 3;
                    try {
                        this.N = Integer.parseInt(args[args.length-1]);
                    } catch (NumberFormatException e) {
                        System.out.printf("Error in last parameter. It was %s. It should be number!\n", args[args.length-1]);
                    }
                    this.query = Arrays.stream(args).skip(2).limit(args.length-3).map(String::toLowerCase).collect(Collectors.joining(" "));
                }
            }
        }
    }


}
