package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;

/*
https://www.w3schools.com/xml/el_attribute.asp

<attribute
default=string
fixed=string
form=qualified|unqualified
id=ID
name=NCName
ref=QName
type=QName
use=optional|prohibited|required
any attributes
>

(annotation?,(simpleType?))

</attribute>
 */
public final class XsdAttribute implements Xsd {
    public static final String Attribute = "attribute";

    public static boolean isAttribute(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Attribute);
    }

    public static ImList<XsdAttribute> parseList(XmlNode el ){
        if( el==null ) throw new IllegalArgumentException("el==null");
        return isAttribute(el)
            ? ImList.first(new XsdAttribute((XmlElem) el))
            : ImList.empty();
    }

    public final XmlElem elem;

    public XsdAttribute(XmlElem elem) {
        if( elem==null ) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
    }
}
