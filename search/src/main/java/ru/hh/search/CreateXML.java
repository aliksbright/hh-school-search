package ru.hh.search;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.PrintWriter;

/**
 * Создает xml файл для индекса.
 * @author Andrey Bukhtoyarov (andreymedoed@gmail.com).
 * @version %Id%.
 * @since 0.1.
 */
public class CreateXML {

    private DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    private DocumentBuilder docBuilder;
    private Document doc;
    private Element root;

    private PrintWriter writer;

    public CreateXML(PrintWriter writer) {
        this.writer = writer;
        prepare();
    }

    private void prepare() {
        try {
            docBuilder = docFactory.newDocumentBuilder();
            doc = docBuilder.newDocument();
            root = doc.createElement("documents");
            this.doc.appendChild(this.root);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void addElement(String documentId, String lineId, String line) {
        Element document = this.doc.createElement("document");
        document.setAttribute("documentId", documentId);
        document.setAttribute("lineId", lineId);
        document.appendChild(this.doc.createTextNode(line));
        this.root.appendChild(document);
    }

    public void print() {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(this.doc), new StreamResult(this.writer));
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
}
