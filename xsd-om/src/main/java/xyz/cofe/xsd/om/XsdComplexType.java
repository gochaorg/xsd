package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;
import java.util.Optional;

/*
https://www.w3schools.com/xml/el_complextype.asp

<complexType
id=ID
name=NCName
abstract=true|false
mixed=true|false
block=(#all|list of (extension|restriction))
final=(#all|list of (extension|restriction))
any attributes
>

(annotation?,(simpleContent|complexContent|((group|all|
choice|sequence)?,((attribute|attributeGroup)*,anyAttribute?))))

</complexType>

--------------
id	        Optional. Specifies a unique ID for the element
name	    Optional. Specifies a name for the element
abstract	Optional. Specifies whether the complex type can be used in an instance document. True indicates that an element cannot use this complex type directly but must use a complex type derived from this complex type. Default is false
mixed	    Optional. Specifies whether character data is allowed to appear between the child elements of this complexType element. Default is false. If a simpleContent element is a child element, the mixed attribute is not allowed!
block	    Optional. Prevents a complex type that has a specified type of derivation from being used in place of this complex type. This value can contain #all or a list that is a subset of extension or restriction:
                extension - prevents complex types derived by extension
                restriction - prevents complex types derived by restriction
                #all - prevents all derived complex types
final	    Optional. Prevents a specified type of derivation of this complex type element. Can contain #all or a list that is a subset of extension or restriction.
                extension - prevents derivation by extension
                restriction - prevents derivation by restriction
                #all - prevents all derivation
any attributes	Optional. Specifies any other attributes with non-schema namespace

 */
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
