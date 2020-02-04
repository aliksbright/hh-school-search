package ru.hh.search;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.Writer;

/**
 * Создает xml файл для индекса.
 * @author Andrey Bukhtoyarov (andreymedoed@gmail.com).
 * @version %Id%.
 * @since 0.1.
 */
public class CreateXML {

    private Document doc;
    private Element root;

    private final Writer writer;

    public CreateXML(Writer writer) throws ParserConfigurationException {
        this.writer = writer;
        prepare();
    }

    private void prepare() throws ParserConfigurationException {
        this.doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        this.root = this.doc.createElement("documents");
        this.doc.appendChild(this.root);
    }

    public void addElement(String documentId, String lineId, String line) {
        Element document = this.doc.createElement("document");
        document.setAttribute("documentId", documentId);
        document.setAttribute("lineId", lineId);
        document.appendChild(this.doc.createTextNode(line));
        this.root.appendChild(document);
    }

    public void save() throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(this.doc), new StreamResult(this.writer));
    }
}
