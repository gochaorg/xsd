package xyz.cofe.xsd.om.xml;

import java.util.List;

public interface XmlElem extends XmlNode {
    String getTagName();

    String getNamespaceURI();

    String getLocalName();

    String getPrefix();

    String getAttribute(String name);

    List<XmlAttr> getAttributes();

    List<XmlNode> getChildren();
}
