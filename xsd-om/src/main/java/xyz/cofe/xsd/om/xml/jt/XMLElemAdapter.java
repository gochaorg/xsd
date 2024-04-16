package xyz.cofe.xsd.om.xml.jt;

import org.w3c.dom.Attr;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import xyz.cofe.im.struct.ImList;
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

    private ImList<XmlAttr> attrs;

    @Override
    public ImList<XmlAttr> getAttributes() {
        if (attrs != null) return attrs;
        var src = element.getAttributes();
        var attrs = ImList.<XmlAttr>empty();
        if (src != null) {
            for (var i = 0; i < src.getLength(); i++) {
                var n = src.item(i);
                if (n instanceof Attr a) {
                    attrs = attrs.prepend(new XMLAttrAdapter(a));
                }
            }
        }
        this.attrs = attrs.reverse();
        return this.attrs;
    }

    private ImList<XmlNode> children;

    @SuppressWarnings("ConstantValue")
    @Override
    public ImList<XmlNode> getChildren() {
        if (children != null) return children;
        var children = ImList.<XmlNode>empty();
        var src = element.getChildNodes();
        if (src != null) {
            for (var i = 0; i < src.getLength(); i++) {
                var node = src.item(i);
                if (node instanceof Element el) {
                    children = children.prepend(new XMLElemAdapter(el));
                } else if (node instanceof Text el) {
                    children = children.prepend(new XMLTextAdapter(el));
                } else if (node instanceof CharacterData el) {
                    children = children.prepend(new XMLCDataAdapter(el));
                }
            }
        }
        this.children = children.reverse();
        return this.children;
    }
}
