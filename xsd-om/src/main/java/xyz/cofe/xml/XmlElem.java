package xyz.cofe.xml;

import xyz.cofe.coll.im.ImList;

public interface XmlElem extends XmlNode {
    String getTagName();

    String getNamespaceURI();

    String getLocalName();

    String getPrefix();

    String getAttribute(String name);

    default ImList<XmlAttr> attrib(String localName){
        if( localName==null ) throw new IllegalArgumentException("localName==null");
        return getAttributes().filter( a -> localName.equals(a.getLocalName()) );
    }

    ImList<XmlAttr> getAttributes();

    ImList<XmlNode> getChildren();
}
