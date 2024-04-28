package xyz.cofe.xml.jt;

import org.w3c.dom.Text;
import xyz.cofe.xml.XmlText;

public class XMLTextAdapter extends XMLNodeAdapter implements XmlText {
    public XMLTextAdapter(Text node) {
        super(node);
    }
}
