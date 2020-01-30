package app.structure;

public class Term {
    private String value;
    private int position;

    public Term(String value, int position) {
        this.value = value;
        this.position = position;
    }

    public String getValue() {
        return value;
    }

    public int getPosition() {
        return position;
    }
}
