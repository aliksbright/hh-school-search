package HHSchoolSearch.Model;

import com.google.gson.annotations.Expose;

public class Token
{
    @Expose
    private int position;
    @Expose
    private int termId;
    @Expose
    private int documentId;

    public int getPosition() { return position; }
    public int getTermId() { return termId; }
    public int getDocumentId() { return documentId; }

    public Token(int _position, Term _term, Document _document)
    {
        position = _position;
        termId = _term.getTermId();
        documentId = _document.getDocId();
    }
}
