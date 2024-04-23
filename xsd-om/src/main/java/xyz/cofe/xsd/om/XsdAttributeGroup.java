package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;

/*
https://www.w3schools.com/xml/el_attributegroup.asp

<attributeGroup
id=ID
name=NCName
ref=QName
any attributes
>

(annotation?),((attribute|attributeGroup)*,anyAttribute?))

</attributeGroup>
 */
public final class XsdAttributeGroup implements Xsd {
    public static final String Name = "attributeGroup";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public static ImList<XsdAttributeGroup> parseList(XmlNode el ){
        if( el==null ) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.first(new XsdAttributeGroup((XmlElem) el))
            : ImList.empty();
    }

    public final XmlElem elem;

    public XsdAttributeGroup(XmlElem elem) {
        if( elem==null ) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
    }
}
