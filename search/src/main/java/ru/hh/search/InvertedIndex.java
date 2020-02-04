package ru.hh.search;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Создает в ОЗУ инвертированный индекс из индекса на диске.
 * @author Andrey Bukhtoyarov (andreymedoed@gmail.com).
 * @version %Id%.
 * @since 0.1.
 */
public class InvertedIndex {
    /**
     * Путь к файлу индекса.
     */
    private final Path index;
    /**
     * Инвертированный индекс.
     */
    private final HashMap<String, ArrayList<Document>> invertedIndex = new HashMap<>();

    public InvertedIndex(Path index) {
        this.index = index;
    }

    /**
     * Парсит индекс с диска и записывает в инвертированный индекс.
     * @return
     */
    public HashMap<String, ArrayList<Document>> createInvIndex() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser;
        try {
            parser = factory.newSAXParser();
            parser.parse(index.toFile(), new IndexReaderHandlers());
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.out.println("Файл индекса сломан или не являетя файлом индекса.");;
        }
        return this.invertedIndex;
    }

    private class IndexReaderHandlers extends DefaultHandler {

        private Document doc;

        @Override
        public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
            if (qName.equals("document")) {
                String lineId = atts.getValue("lineId");
                this.doc = new Document();
                this.doc.setLineId(Integer.valueOf(lineId));
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (this.doc != null) {
                String information = new String(ch, start, length);
                information = information.replace(System.lineSeparator(), "").trim();
                AtomicInteger position = new AtomicInteger();
                Arrays.stream(information.split("/"))
                        .forEach(word -> this.doc.addTerm(new Term(position.getAndIncrement(), word)));
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (this.doc != null) {
                this.doc.getTerms().forEach(term -> {
                    if (invertedIndex.containsKey(term.getValue())) {
                        invertedIndex.get(term.getValue()).add(this.doc);
                    } else {
                        invertedIndex.put(term.getValue(), new ArrayList<>(List.of(this.doc)));
                    }
                });
            }
            this.doc = null;
        }
    }
}
