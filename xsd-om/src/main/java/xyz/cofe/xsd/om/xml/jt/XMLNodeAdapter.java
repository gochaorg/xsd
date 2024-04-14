package xyz.cofe.xsd.om.xml.jt;

import org.w3c.dom.Node;
import xyz.cofe.xsd.om.xml.XmlNode;

public class XMLNodeAdapter implements XmlNode {
    private final Node node;

    public XMLNodeAdapter(Node node) {
        if( node==null ) throw new IllegalArgumentException("node==null");
        this.node = node;
    }

    @Override
    public String getTextContent() {
        return node.getTextContent();
    }
}
