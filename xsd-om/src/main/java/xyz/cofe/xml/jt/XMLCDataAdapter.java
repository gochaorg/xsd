package xyz.cofe.xml.jt;

import org.w3c.dom.CharacterData;
import xyz.cofe.xml.XmlCData;

public class XMLCDataAdapter extends XMLNodeAdapter implements XmlCData {
    public XMLCDataAdapter(CharacterData node) {
        super(node);
    }
}
