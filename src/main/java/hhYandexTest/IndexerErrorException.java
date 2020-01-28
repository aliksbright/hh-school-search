package hhYandexTest;

public class IndexerErrorException extends Exception {
    String reason;

    public IndexerErrorException(String reason) {
        super(reason);
    }
}
