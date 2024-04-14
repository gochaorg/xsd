package xyz.cofe.xsd.om.xml.jt;

import org.w3c.dom.CharacterData;
import xyz.cofe.xsd.om.xml.XmlCData;

public class XMLCDataAdapter extends XMLNodeAdapter implements XmlCData {
    public XMLCDataAdapter(CharacterData node) {
        super(node);
    }
}
