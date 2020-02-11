package HHSchoolSearch.Model;

import com.google.gson.annotations.Expose;

import java.util.*;

public class Document
{
    @Expose
    private int docId;
    @Expose
    private String docName;
    @Expose
    private String docContent;

    @Expose
    private HashMap<Integer, Token> docPositionTokensMap;

    public int getDocId() { return docId; }
    public String getDocName() { return docName; }
    public String getDocContent() { return docContent; }
    public Set<Token> getTokens() { return new HashSet<>(docPositionTokensMap.values()); }

    public Document(int id, String name, String content)
    {
        docId = id;
        docName = name;
        docContent = content;

        docPositionTokensMap = new HashMap<>();
    }

    public void putToken(Token token)
    {
        docPositionTokensMap.put(token.getPosition(), token);
    }

    public void putTokens(Collection<Token> tokens)
    {
        for (var token : tokens) putToken(token);
    }

    public Token getTokenAt(int position)
    {
        return docPositionTokensMap.getOrDefault(position, null);
    }
}
