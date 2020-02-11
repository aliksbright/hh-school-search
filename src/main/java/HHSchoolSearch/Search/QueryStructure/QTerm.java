package HHSchoolSearch.Search.QueryStructure;

public class QTerm extends QOperand
{
    private String termString;

    public QTerm(String _termString) { setTermString(_termString); }

    public String getTermString() { return termString; }

    public void setTermString(String termString) { this.termString = termString; }
}
