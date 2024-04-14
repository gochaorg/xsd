package xyz.cofe.xsd.om.xml.jso;

import org.teavm.jso.dom.xml.Node;
import xyz.cofe.xsd.om.xml.XmlCData;
import xyz.cofe.xsd.om.xml.XmlNode;

public class XmlCDataJSOAdapter extends XmlNode.XmlNodeJSOAdapter implements XmlCData {
    public XmlCDataJSOAdapter(Node node) {
        super(node);
    }
}
