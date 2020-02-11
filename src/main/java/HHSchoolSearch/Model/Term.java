package HHSchoolSearch.Model;

import com.google.gson.annotations.Expose;

public class Term
{
    @Expose
    private int termId;
    @Expose
    private String termString;

    public int getTermId() { return termId; }
    public String getTermString() { return termString; }

    public Term(int id, String string)
    {
        termId = id;
        termString = string;
    }

    @Override
    public int hashCode()
    {
        return termString != null
                ? termString.hashCode()
                : 0;
    }
}
