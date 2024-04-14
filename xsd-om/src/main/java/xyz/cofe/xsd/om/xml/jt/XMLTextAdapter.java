package xyz.cofe.xsd.om.xml.jt;

import org.w3c.dom.Text;
import xyz.cofe.xsd.om.xml.XmlText;

public class XMLTextAdapter extends XMLNodeAdapter implements XmlText {
    public XMLTextAdapter(Text node) {
        super(node);
    }
}
