package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.im.struct.Result;
import xyz.cofe.xsd.om.BuiltInTypes.ID;
import xyz.cofe.xsd.om.BuiltInTypes.BOOLEAN;
import xyz.cofe.xsd.om.xml.XmlAttr;
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
                                             TypeDef,
                                             IDAttribute,
                                             NameAttribute,
                                             XsdAnnotation.AnnotationProperty
{
    public static final String Name = "complexType";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public static ImList<XsdComplexType> parseList(XmlNode el) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.first(new XsdComplexType((XmlElem) el))
            : ImList.empty();
    }

    public final XmlElem elem;

    @Override
    public XmlElem elem() {
        return elem;
    }

    public XsdComplexType(XmlElem elem) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
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

    public Result<BOOLEAN,String> getAbstract(){
        return Result.of(elem.attrib("abstract").head(), "abstract not found")
            .map(XmlAttr::getValue)
            .flatMap(BOOLEAN::parse);
    }

    public Result<BOOLEAN,String> getMixed(){
        return Result.of(elem.attrib("mixed").head(), "mixed not found")
            .map(XmlAttr::getValue)
            .flatMap(BOOLEAN::parse);
    }

    public Result<String,String> getBlock(){
        return Result.of(elem.attrib("block").head(), "block not found")
            .map(XmlAttr::getValue);
    }

    public Result<String,String> getFinal(){
        return Result.of(elem.attrib("final").head(), "final not found")
            .map(XmlAttr::getValue);
    }
}
