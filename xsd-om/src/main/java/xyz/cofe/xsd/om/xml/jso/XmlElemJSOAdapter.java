package xyz.cofe.xsd.om.xml.jso;

import org.teavm.jso.dom.xml.Element;
import org.teavm.jso.dom.xml.Node;
import xyz.cofe.xsd.om.xml.XmlAttr;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.ArrayList;
import java.util.List;

public class XmlElemJSOAdapter extends XmlNode.XmlNodeJSOAdapter implements XmlElem {
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

    private List<XmlAttr> attributes;

    @Override
    public List<XmlAttr> getAttributes() {
        if( attributes!=null )return attributes;
        attributes = new ArrayList<>();
        var atts = element.getAttributes();
        for( var i=0;i<atts.getLength();i++ ){
            attributes.add(new XMLAttrJSOAdapter(atts.get(i)));
        }
        return attributes;
    }

    private List<XmlNode> children;

    @Override
    public List<XmlNode> getChildren() {
        if (children != null) return children;
        children = new ArrayList<>();

        var nodeList = element.getChildNodes();
        if (nodeList != null) {
            for (var i = 0; i < nodeList.getLength(); i++) {
                var child = nodeList.get(i);
                if (child != null) {
                    switch (child.getNodeType()) {
                        case Node.ELEMENT_NODE -> {
                            var el = new XmlElemJSOAdapter((Element) child);
                            children.add(el);
                        }
                        case Node.TEXT_NODE -> {
                            children.add(new XmlTextJSOAdapter(child));
                        }
                        case Node.CDATA_SECTION_NODE -> {
                            children.add(new XmlCDataJSOAdapter(child));
                        }
                        case Node.COMMENT_NODE -> {}
                        case Node.DOCUMENT_FRAGMENT_NODE -> {}
                    }
                }
            }
        }

        return children;
    }
}
