package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;

/*
https://www.w3schools.com/xml/el_anyattribute.asp

<anyAttribute
id=ID
namespace=namespace
processContents=lax|skip|strict
any attributes
>

(annotation?)

</anyAttribute>
 */
public final class XsdAnyAttribute implements Xsd {
    public static final String AnyAttribute = "anyAttribute";

    public static boolean isAttribute(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), AnyAttribute);
    }

    public static ImList<XsdAnyAttribute> parseList(XmlNode el ){
        if( el==null ) throw new IllegalArgumentException("el==null");
        return isAttribute(el)
            ? ImList.first(new XsdAnyAttribute((XmlElem) el))
            : ImList.empty();
    }

    public final XmlElem elem;

    public XsdAnyAttribute(XmlElem elem) {
        if( elem==null ) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
    }
}
