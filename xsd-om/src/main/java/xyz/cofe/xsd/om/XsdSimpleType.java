package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;

/*
https://www.w3schools.com/xml/el_simpletype.asp

<simpleType
id=ID
name=NCName
any attributes
>

(annotation?,(restriction|list|union))

</simpleType>
 */
public final class XsdSimpleType implements Xsd, TypeDef {
    public static final String SimpleType = "simpleType";

    public static boolean isAttribute(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), SimpleType);
    }

    public static ImList<XsdSimpleType> parseList(XmlNode el) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isAttribute(el)
            ? ImList.first(new XsdSimpleType((XmlElem) el))
            : ImList.empty();
    }

    public final XmlElem elem;

    public XsdSimpleType(XmlElem elem) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
    }
}
