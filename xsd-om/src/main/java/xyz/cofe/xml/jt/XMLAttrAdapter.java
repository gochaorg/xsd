package xyz.cofe.xml.jt;

import org.w3c.dom.Attr;
import xyz.cofe.xml.XmlAttr;

public class XMLAttrAdapter implements XmlAttr {
    private final Attr attr;

    public XMLAttrAdapter(Attr attr) {
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
