package xyz.cofe.xml.jso;

import org.teavm.jso.dom.xml.Node;
import xyz.cofe.xml.XmlCData;

public class XmlCDataJSOAdapter extends XmlNodeJSOAdapter implements XmlCData {
    public XmlCDataJSOAdapter(Node node) {
        super(node);
    }
}
