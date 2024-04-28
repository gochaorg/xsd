package xyz.cofe.xml.jso;

import org.teavm.jso.dom.xml.Node;
import xyz.cofe.xml.XmlNode;

public class XmlNodeJSOAdapter implements XmlNode {
    private final Node node;

    public XmlNodeJSOAdapter(Node node) {
        this.node = node;
    }

    @Override
    public String getTextContent() {
        return node.getTextContent();
    }
}
