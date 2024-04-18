package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;
import java.util.Optional;

public final class XsdComplexType implements Xsd,
                                             TypeDef {
    public static final String ComplexType = "complexType";

    public static boolean isAttribute(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), ComplexType);
    }

    public static ImList<XsdComplexType> parseList(XmlNode el) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isAttribute(el)
            ? ImList.first(new XsdComplexType((XmlElem) el))
            : ImList.empty();
    }

    public final XmlElem elem;

    public XsdComplexType(XmlElem elem) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
    }

    public ImList<XsdAnnotation> getAnnotations() {
        return elem.getChildren().flatMap(XsdAnnotation::parseList);
    }

    public Optional<ContentDef> getContentDef() {
        var simpleContent = elem.getChildren().flatMap(XsdSimpleContent::parseList).head();
        if (simpleContent.isPresent()) return simpleContent.map(a -> a);

        var complexContent = elem.getChildren().flatMap(XsdComplexContent::parseList).head();
        if (complexContent.isPresent()) return complexContent.map(a -> a);

        Optional<ElementsLayout> group = elem.getChildren().flatMap(XsdGroup::parseList).head().map(a -> a);
        Optional<ElementsLayout> all = elem.getChildren().flatMap(XsdAll::parseList).head().map(a -> a);
        Optional<ElementsLayout> choice = elem.getChildren().flatMap(XsdChoice::parseList).head().map(a -> a);
        Optional<ElementsLayout> seq = elem.getChildren().flatMap(XsdSequence::parseList).head().map(a -> a);
        Optional<ElementsLayout> elemLayout =
            group.or(()->all)
                .or(()->choice)
                .or(()->seq);

        ImList<XsdAttribute> attr = elem.getChildren().flatMap(XsdAttribute::parseList);
        ImList<XsdAttributeGroup> attrGroup = elem.getChildren().flatMap(XsdAttributeGroup::parseList);
        Optional<XsdAnyAttribute> anyAttr = elem.getChildren().flatMap(XsdAnyAttribute::parseList).head();

        return Optional.of(new ElementContent(
            elemLayout,
            attr,
            attrGroup,
            anyAttr
        ));
    }
}
