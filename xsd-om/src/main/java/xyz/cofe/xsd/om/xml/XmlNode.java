package xyz.cofe.xsd.om.xml;

import org.teavm.jso.dom.xml.Node;

public interface XmlNode {
    String getTextContent();

    static class XmlNodeJSOAdapter implements XmlNode {
        private final Node node;

        public XmlNodeJSOAdapter(Node node) {
            this.node = node;
        }

        @Override
        public String getTextContent() {
            return node.getTextContent();
        }
    }
}
