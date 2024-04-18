package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;

/*
https://www.w3schools.com/xml/el_key.asp

<key
id=ID
name=NCName
any attributes
>

(annotation?,(selector,field+))

</key>
 */
public final class XsdKey implements Xsd {
    public static final String Key = "key";

    public static boolean isAttribute(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Key);
    }

    public static ImList<XsdKey> parseList(XmlNode el) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isAttribute(el)
            ? ImList.first(new XsdKey((XmlElem) el))
            : ImList.empty();
    }

    public final XmlElem elem;

    public XsdKey(XmlElem elem) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
    }
}
