package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.im.struct.Result;
import xyz.cofe.xsd.om.xml.XmlAttr;
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
( annotation?,
  (simpleType?,(minExclusive|minInclusive|maxExclusive|maxInclusive|totalDigits|fractionDigits|length|minLength|maxLength|enumeration|whiteSpace|pattern)*)
)

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
public final class XsdRestriction implements Xsd,
                                             SimpleTypeContent,
                                             XsdComplexContent.Nested,
                                             BaseAttribute,
                                             IDAttribute
{
    public static final String Name = "restriction";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public static ImList<XsdRestriction> parseList(XmlNode el) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.first(new XsdRestriction((XmlElem) el))
            : ImList.empty();
    }

    public final XmlElem elem;

    @Override
    public XmlElem elem() {
        return elem;
    }

    public XsdRestriction(XmlElem elem) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
    }

    public ImList<Restriction> getRestrictions(){
        return elem.getChildren().flatMap(Restriction::parseList);
    }
}
