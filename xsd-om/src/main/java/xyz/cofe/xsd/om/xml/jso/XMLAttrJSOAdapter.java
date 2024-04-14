package xyz.cofe.xsd.om.xml.jso;

import org.teavm.jso.dom.xml.Attr;
import xyz.cofe.xsd.om.xml.XmlAttr;

public class XMLAttrJSOAdapter implements XmlAttr {
    private final Attr attr;

    public XMLAttrJSOAdapter(Attr attr) {
        this.attr = attr;
    }

    @Override
    public String getName() {
        return attr.getName();
    }

    @Override
    public String getLocalName() {
        return attr.getLocalName();
    }

    @Override
    public String getPrefix() {
        return attr.getPrefix();
    }

    @Override
    public String getNamespaceURI() {
        return attr.getNamespaceURI();
    }

    @Override
    public String getValue() {
        return attr.getValue();
    }

    @Override
    public String getNodeName() {
        return attr.getNodeName();
    }
}
