package hhYandexTest.Searcher;

import hhYandexTest.InverseIndexer.DocInfo;
import hhYandexTest.InverseIndexer.InverseIndex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SearchRequest {
    SearchEntry compiledRequest;

    List<String> requestInflate(String left, String right, ArrayList<String> blocks) {
        int n;
        for (n=0;n<blocks.size();n++) {
           if (left.lastIndexOf("#"+n+"#") >= 0)
               left = left.replaceAll("#"+n+"#", blocks.get(n));
           else
               right = right.replaceAll("#"+n+"#", blocks.get(n));

        }

        return List.of(left, right);
    }

    SearchEntry createEntry(String entryText) throws Exception {
        String      simplified;
        int         pos = 0 ;
        ArrayList<String> blocks = new ArrayList<>(5);

        simplified = "";

        while (pos < entryText.length()) {
            char curchar = entryText.charAt(pos);

            if (curchar == ')')
                throw new Exception("Invalid request : closing bracket without opening at pos " + pos);

            if (curchar != '(') {
                simplified = simplified + curchar;
                pos++;
                continue;
            }

            int blockStartPos = pos + 1;
            int nested = 0;

            while (pos < entryText.length()) {
                if (entryText.charAt(pos) == '(')
                    nested++;
                if (entryText.charAt(pos) == ')') {
                    nested--;
                    if (nested == 0)
                        break;
                }
                pos++;
            }

            if (nested != 0)
                throw new Exception("Incorrect request : non paired opening bracket");

            if (blockStartPos == pos)
                throw new Exception("Invalid request : empty brackets");

            simplified = simplified + "#" + blocks.size()+"# ";
            blocks.add(entryText.substring(blockStartPos, pos));

            pos++;
        }

        // Cleanup spaces after bracket removal (if any)
        simplified = simplified.replaceAll("\\s+", " ");
        simplified = simplified.replaceAll("^\\s+|\\s+$", "");

        String [] itemList  = simplified.split(" ");

        int lastNear = -1;
        int lastOr = -1;
        int lastAnd = -1;
        int lastNot = -1;
        int lastPos = 0;

        for (String s : itemList) {
            if (s.equals("OR"))
                lastOr = lastPos;
            if (s.equals("AND"))
                lastAnd = lastPos;
            if (s.equals("NOT"))
                lastNot = lastPos;
            if (s.equals("NEAR"))
                lastNear = lastPos;

            lastPos += s.length() + 1;
        }

        if ((lastOr == itemList.length - 1) ||
            (lastAnd == itemList.length -1) ||
            (lastNot == itemList.length -1))
            throw new Exception("Incorrect request : function without right operand");

        if ((lastOr == 0) ||
            (lastAnd == 0))
            throw new Exception("Incorrect request : function without left operand");

        String left ="", right = "";
        SearchEntry     entry = null;

        if (lastOr > -1) {
            left = simplified.substring(0, lastOr - 1);
            right = simplified.substring(lastOr + 3);
            entry = new SearchOrEntry();
        } else if (lastAnd > -1) {
            left = simplified.substring(0, lastAnd - 1);
            right = simplified.substring(lastAnd + 4);
            entry = new SearchAndEntry();
        } else if (lastNot > -1) {
            left = "";
            right = simplified.substring(lastNot + 4);
            entry = new SearchNotEntry();
        } else if (lastNear > -1) {
            left = simplified.substring(0, lastNear - 1);
            right = simplified.substring(lastNear + 5);
            entry = new SearchAndEntry(true);
        } else {
            if (blocks.size() == 0) {
                // One term request
                if (itemList.length == 1)
                    return new SearchTermEntry(itemList[0]);

                // Phrase -> convert to "<term> NEAR <term> ..." and process
                String phrase = List.of(itemList).stream().collect(Collectors.joining(" NEAR "));
                return createEntry(phrase);
            } else {
                // Inflate and process again
                List<String> inflated = requestInflate(simplified, "", blocks);
                return createEntry(inflated.get(0));
            }

        }

        // Inflate
        if (blocks.size() > 0) {
            List<String> inflated = requestInflate(left, right, blocks);
            left    = inflated.get(0);
            right   = inflated.get(1);
        }

        // System.out.println(entryText + " -> (" + left  + ") x (" + right+")");

        entry.setLeft(createEntry(left));
        entry.setRight(createEntry(right));

        return entry;
    }


    public static boolean requestItemIsOk(String item) {
        if (
                item.equals("OR") ||
                item.equals("AND") ||
                item.equals("NOT") ||
                item.equals("NEAR")
        ) return true;

        if (item.length() < 3)
            return false;

        if (item.matches("^.+\\(.*|.*\\).+$"))
            return false;

        return true;
    }


    public SearchRequest(String request) throws Exception {
        String req;
        // Cleanup
        req = request.replaceAll("[!~?#$%^&*_\\-=+'\"><\\[\\]{}.,:;]", "");
        req = req.replaceAll("\\s+", " ");
        req = req.replaceAll("^\\s+|\\s+$", "");

        //System.out.println("Filtered request " + req);

        String [] itemList  = req.split(" ");

        String rebuiltQuery = Arrays.stream(itemList)
            .filter( SearchRequest::requestItemIsOk)
            .collect(Collectors.joining(" "));

        compiledRequest = createEntry(rebuiltQuery);
    }

    public void setInverseIndex(InverseIndex index) {
        compiledRequest.setInverseIndex(index);
    }

    public void test() {
        compiledRequest.test();
        System.out.println();
    }

    public DocInfo pull() {
        return compiledRequest.pull();
    }

    public void setTotalDocsuments(long totalDocuments) {
        compiledRequest.setTotalDocsuments(totalDocuments);
    }
}
