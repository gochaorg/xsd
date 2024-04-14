package xyz.cofe.xsd.om.xml.jt;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import xyz.cofe.xsd.om.xml.XmlDoc;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.print.XmlPrinter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

public class XMLDocAdapter extends XMLNodeAdapter implements XmlDoc {
    private final Document document;
    public XMLDocAdapter(Document node) {
        super(node);
        this.document = node;
    }

    private XmlElem docElem;

    @Override
    public XmlElem getDocumentElement() {
        if( docElem!=null )return docElem;
        docElem = new XMLElemAdapter(document.getDocumentElement());
        return docElem;
    }

    public static XMLDocAdapter parse(String xml){
        if( xml==null ) throw new IllegalArgumentException("xml==null");

        var dbf = DocumentBuilderFactory.newInstance();
        try {
            dbf.setNamespaceAware(true);

            var db = dbf.newDocumentBuilder();
            InputSource src = new InputSource(new StringReader(xml));
            var doc = db.parse(src);

            return new XMLDocAdapter(doc);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }
}
