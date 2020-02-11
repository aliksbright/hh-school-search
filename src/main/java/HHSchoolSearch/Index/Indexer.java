package HHSchoolSearch.Index;

import HHSchoolSearch.Model.Document;

import java.util.List;

public class Indexer
{
    public static void indexLinesAsDocs(String fileName, List<String> lines)
    {
        for (var docLine : lines)
        {
            var docName = String.format("%s[%d]", fileName, lines.indexOf(docLine));
            // вынести factory или пусть так
            var docId = AllDocs.issueId();
            var doc = new Document(docId, docName, docLine);

            Tokenizer.tokenize(doc, true);

            AllDocs.registerDocument(doc);
            InvertedTerms.registerEntriesInDoc(doc);
            InvertedDocs.registerEntriesInDoc(doc);
        }
    }
}
