package HHSchoolSearch.Index;

import HHSchoolSearch.Model.Document;
import com.google.gson.annotations.Expose;

import java.util.HashMap;

public class AllDocs
{
    @Expose
    private int lastIssuedId;
    @Expose
    private HashMap<Integer, Document> docsIdMap;

    private static AllDocs singleton;
    private AllDocs() { }

    public static boolean initialize(AllDocs providedSingleton) throws Exception
    {
        if (providedSingleton != null)
        {
            if (providedSingleton.docsIdMap == null)
                throw new Exception("Invalid JSON content in res/index/all_docs.json");

            singleton = providedSingleton;
        }
        else
        {
            singleton = new AllDocs();
            singleton.lastIssuedId = -1;
            singleton.docsIdMap = new HashMap<>();
        }

        return true;
    }

    public static void registerDocument(Document doc)
    {
        var docId = doc.getDocId();
        getDocsIdMap().put(docId, doc);
    }

    public static boolean containsDoc(int docId)
    {
        return getDocsIdMap().containsKey(docId);
    }

    public static Document getDoc(int docId)
    {
        return getDocsIdMap().getOrDefault(docId, null);
    }

    public static int issueId()
    {
        var newId = getLastIssuedId() + 1;
        singleton.lastIssuedId = newId;

        return newId;
    }

    public static AllDocs getSingleton() { return singleton; }
    public static int getLastIssuedId() { return singleton.lastIssuedId; }
    public static HashMap<Integer, Document> getDocsIdMap() { return singleton.docsIdMap; }
}
