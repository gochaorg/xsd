package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;

/*
https://www.w3schools.com/xml/el_list.asp

<list
id=ID
itemType=QName
any attributes
>

(annotation?,(simpleType?))

</list>
 */
public final class XsdList implements Xsd {
    public static final String List = "list";

    public static boolean isAttribute(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), List);
    }

    public static ImList<XsdList> parseList(XmlNode el) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isAttribute(el)
            ? ImList.first(new XsdList((XmlElem) el))
            : ImList.empty();
    }

    public final XmlElem elem;

    public XsdList(XmlElem elem) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
    }
}
