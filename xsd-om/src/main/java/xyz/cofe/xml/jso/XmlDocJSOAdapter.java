package xyz.cofe.xml.jso;

import org.teavm.jso.dom.xml.DOMParser;
import org.teavm.jso.dom.xml.Document;
import xyz.cofe.xml.XmlDoc;
import xyz.cofe.xml.XmlElem;

public class XmlDocJSOAdapter implements XmlDoc {
    private final Document document;

    public XmlDocJSOAdapter(Document document) {
        this.document = document;
    }

    private XmlElem docElem;

    @Override
    public XmlElem getDocumentElement() {
        if (docElem != null) return docElem;

        var el = document.getDocumentElement();
        if (el != null) {
            docElem = new XmlElemJSOAdapter(el);
        }

        return docElem;
    }

    public static XmlDocJSOAdapter parse(String xml){
        if( xml==null ) throw new IllegalArgumentException("xml==null");
        var doc = DOMParser.create().parseFromString(xml, "text/xml");
        return new XmlDocJSOAdapter(doc);
    }
}
