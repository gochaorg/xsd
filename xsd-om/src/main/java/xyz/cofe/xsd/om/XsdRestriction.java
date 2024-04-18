package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;

/*
https://www.w3schools.com/xml/el_restriction.asp

<restriction
id=ID
base=QName
any attributes
>

Content for simpleType:
(annotation?,(simpleType?,(minExclusive|minInclusive|
maxExclusive|maxInclusive|totalDigits|fractionDigits|
length|minLength|maxLength|enumeration|whiteSpace|pattern)*))

Content for simpleContent:
(annotation?,(simpleType?,(minExclusive |minInclusive|
maxExclusive|maxInclusive|totalDigits|fractionDigits|
length|minLength|maxLength|enumeration|whiteSpace|pattern)*)?,
((attribute|attributeGroup)*,anyAttribute?))

Content for complexContent:
(annotation?,(group|all|choice|sequence)?,
((attribute|attributeGroup)*,anyAttribute?))

</restriction>
 */
public final class XsdRestriction implements Xsd {
    public static final String Restriction = "restriction";

    public static boolean isAttribute(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Restriction);
    }

    public static ImList<XsdRestriction> parseList(XmlNode el) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isAttribute(el)
            ? ImList.first(new XsdRestriction((XmlElem) el))
            : ImList.empty();
    }

    public final XmlElem elem;

    public XsdRestriction(XmlElem elem) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
    }
}
