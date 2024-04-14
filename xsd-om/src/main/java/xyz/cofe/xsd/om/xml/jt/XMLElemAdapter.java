package xyz.cofe.xsd.om.xml.jt;

import org.w3c.dom.Attr;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import xyz.cofe.xsd.om.xml.XmlAttr;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.ArrayList;
import java.util.List;

public class XMLElemAdapter extends XMLNodeAdapter implements XmlElem {
    private final Element element;

    public XMLElemAdapter(Element node) {
        super(node);
        this.element = node;
    }

    @Override
    public String getTagName() {
        return element.getTagName();
    }

    @Override
    public String getNamespaceURI() {
        return element.getNamespaceURI();
    }

    @Override
    public String getLocalName() {
        return element.getLocalName();
    }

    @Override
    public String getPrefix() {
        return element.getPrefix();
    }

    @Override
    public String getAttribute(String name) {
        return element.getAttribute(name);
    }

    private List<XmlAttr> attrs;

    @Override
    public List<XmlAttr> getAttributes() {
        if (attrs != null) return attrs;
        var src = element.getAttributes();
        attrs = new ArrayList<>();
        if (src != null) {
            for (var i = 0; i < src.getLength(); i++) {
                var n = src.item(i);
                if (n instanceof Attr a) {
                    attrs.add(new XMLAttrAdapter(a));
                }
            }
        }
        return attrs;
    }

    private List<XmlNode> children;

    @SuppressWarnings("ConstantValue")
    @Override
    public List<XmlNode> getChildren() {
        if (children != null) return children;
        children = new ArrayList<>();
        var src = element.getChildNodes();
        if (src != null) {
            for (var i = 0; i < src.getLength(); i++) {
                var node = src.item(i);
                if (node instanceof Element el) {
                    children.add(new XMLElemAdapter(el));
                } else if (node instanceof Text el) {
                    children.add(new XMLTextAdapter(el));
                } else if (node instanceof CharacterData el) {
                    children.add(new XMLCDataAdapter(el));
                }
            }
        }
        return children;
    }
}
