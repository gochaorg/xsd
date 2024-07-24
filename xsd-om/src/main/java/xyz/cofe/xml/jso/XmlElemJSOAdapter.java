package xyz.cofe.xml.jso;

import org.teavm.jso.dom.xml.Element;
import org.teavm.jso.dom.xml.Node;
import xyz.cofe.coll.im.ImList;
import xyz.cofe.xml.XmlAttr;
import xyz.cofe.xml.XmlElem;
import xyz.cofe.xml.XmlNode;

public class XmlElemJSOAdapter extends XmlNodeJSOAdapter implements XmlElem {
    private final Element element;

    public XmlElemJSOAdapter(Element element) {
        super(element);
        this.element = element;
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
    public String getPrefix() {
        return element.getPrefix();
    }

    @Override
    public String getLocalName() {
        return element.getLocalName();
    }

    @Override
    public String getAttribute(String name) {
        return element.getAttribute(name);
    }

    private ImList<XmlAttr> attributes;

    @Override
    public ImList<XmlAttr> getAttributes() {
        if( attributes!=null )return attributes;
        var attributes = ImList.<XmlAttr>of();
        var atts = element.getAttributes();
        for( var i=0;i<atts.getLength();i++ ){
            attributes = attributes.prepend(new XMLAttrJSOAdapter(atts.get(i)));
        }
        this.attributes = attributes.reverse();
        return attributes;
    }

    private ImList<XmlNode> children;

    @Override
    public ImList<XmlNode> getChildren() {
        if (children != null) return children;
        var children = ImList.<XmlNode>of();

        var nodeList = element.getChildNodes();
        if (nodeList != null) {
            for (var i = 0; i < nodeList.getLength(); i++) {
                var child = nodeList.get(i);
                if (child != null) {
                    switch (child.getNodeType()) {
                        case Node.ELEMENT_NODE -> {
                            var el = new XmlElemJSOAdapter((Element) child);
                            children = children.prepend(el);
                        }
                        case Node.TEXT_NODE -> {
                            children = children.prepend(new XmlTextJSOAdapter(child));
                        }
                        case Node.CDATA_SECTION_NODE -> {
                            children = children.prepend(new XmlCDataJSOAdapter(child));
                        }
                        case Node.COMMENT_NODE -> {}
                        case Node.DOCUMENT_FRAGMENT_NODE -> {}
                    }
                }
            }
        }

        this.children = children.reverse();
        return this.children;
    }
}
