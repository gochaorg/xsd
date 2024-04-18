package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;

/*
https://www.w3schools.com/xml/el_redefine.asp

<redefine
id=ID
schemaLocation=anyURI
any attributes
>

(annotation|(simpleType|complexType|group|attributeGroup))*

</redefine>
 */
public final class XsdRedefine implements Xsd {
    public static final String Redefine = "redefine";

    public static boolean isAttribute(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Redefine);
    }

    public static ImList<XsdRedefine> parseList(XmlNode el) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isAttribute(el)
            ? ImList.first(new XsdRedefine((XmlElem) el))
            : ImList.empty();
    }

    public final XmlElem elem;

    public XsdRedefine(XmlElem elem) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
    }
}
