package searcher;

import index.Dictionary;
import index.Index;


public class Searcher {
    private Index index;
    private Dictionary dictionary;

    public Searcher(Index index, Dictionary dictionary) {
        this.index = index;
        this.dictionary = dictionary;
    }

    public void printDocumentsFromIndex(String request) {
        dictionary.getTermDictionary().get(request)
                .forEach(id -> System.out.println(id + " " + index.getIndexMap().get(id)));
    }

    public Index getIndex() {
        return index;
    }

    public Dictionary getDictionary() {
        return dictionary;
    }

    public void setIndex(Index index) {
        this.index = index;
    }

    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }
}
