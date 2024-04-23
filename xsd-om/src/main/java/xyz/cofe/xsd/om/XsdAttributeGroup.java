package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.im.struct.Result;
import xyz.cofe.xsd.om.xml.XmlAttr;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;
import java.util.Optional;

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
public final class XsdAttributeGroup implements Xsd,
                                                IDAttribute,
                                                NameAttribute,
                                                RefAttribute,
                                                XsdAnnotation.AnnotationProperty {
    public static final String Name = "attributeGroup";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public static ImList<XsdAttributeGroup> parseList(XmlNode el) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.first(new XsdAttributeGroup((XmlElem) el))
            : ImList.empty();
    }

    public final XmlElem elem;

    @Override
    public XmlElem elem() {
        return elem;
    }

    public XsdAttributeGroup(XmlElem elem) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
    }

    public ImList<XsdAttribute> getAttributes(){ return elem().getChildren().flatMap(XsdAttribute::parseList); }
    public ImList<XsdAttributeGroup> getAttributeGroups(){ return elem().getChildren().flatMap(XsdAttributeGroup::parseList); }
    public Optional<XsdAnyAttribute> getAnyAttribute(){ return elem().getChildren().flatMap(XsdAnyAttribute::parseList).head(); }
}
