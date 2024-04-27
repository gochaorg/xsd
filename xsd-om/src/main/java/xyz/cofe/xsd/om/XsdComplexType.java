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

/**
 The <a href="https://www.w3schools.com/xml/el_complextype.asp">complexType</a> element defines a complex type. A complex type element is an XML element that contains other elements and/or attributes.

<pre>
&lt;complexType
id=ID ?
name=NCName ?
abstract=true|false ?
mixed=true|false ?
block=(#all|list of ({@link XsdExtension extension}|{@link XsdRestriction restriction})) ?
final=(#all|list of ({@link XsdExtension extension}|{@link XsdRestriction restriction})) ?
any attributes ?
&gt;

({@link XsdAnnotation annotation}?,
 ( {@link XsdSimpleContent simpleContent}
 | {@link XsdComplexContent complexContent}
 | (
     ({@link XsdGroup group}|{@link XsdAll all}|{@link XsdChoice choice}|{@link XsdSequence sequence})?,
     (({@link XsdAttribute attribute}|{@link XsdAttributeGroup attributeGroup})*,
       {@link XsdAnyAttribute anyAttribute}?
     ))
   )
)

&lt;/complexType&gt;
</pre>
 
<table>
 <tr style="background-color:#ddd">
    <td>id</td>
    <td>Optional. Specifies a unique ID for the element</td>
 </tr>

 <tr>
 <td>name
 </td>
 <td>Optional. Specifies a name for the element</td>
 </tr>

 <tr style="background-color:#ddd">
 <td>abstract
 </td>
 <td>Optional. Specifies whether the complex type can be used in an instance document. True indicates that an element cannot use this complex type directly but must use a complex type derived from this complex type. Default is false</td>
 </tr>

 <tr>
 <td>mixed
 </td>
 <td>Optional. Specifies whether character data is allowed to appear between the child elements of this complexType element. Default is false. If a simpleContent element is a child element, the mixed attribute is not allowed!</td>
 </tr>

 <tr style="background-color:#ddd">
 <td>block
 </td><td>
    Optional. Prevents a complex type that has a specified type of derivation from being used in place of this complex type. This value can contain #all or a list that is a subset of extension or restriction:
    <br/>  extension - prevents complex types derived by extension
    <br/>  restriction - prevents complex types derived by restriction
    <br/>     #all - prevents all derived complex types
 </td>
 </tr>

 <tr>
 <td>
final
 </td><td>Optional. Prevents a specified type of derivation of this complex type element. Can contain #all or a list that is a subset of extension or restriction.
    <br/>            extension - prevents derivation by extension
    <br/>            restriction - prevents derivation by restriction
    <br/>            #all - prevents all derivation</td>
 </tr>

 <tr style="background-color:#ddd">
 <td>
any attributes
 </td><td>Optional. Specifies any other attributes with non-schema namespace
 </td>
 </tr>
 </table>

 */
@SuppressWarnings({"unused", "OptionalUsedAsFieldOrParameterType", "OptionalAssignedToNull"})
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

    public static ImList<XsdComplexType> parseList(XmlNode el, Xsd parent) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.first(new XsdComplexType((XmlElem) el, parent))
            : ImList.empty();
    }

    public final XmlElem elem;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public final Optional<Xsd> parent;

    @Override
    public Optional<Xsd> getParent() {
        return parent;
    }

    @Override
    public XmlElem elem() {
        return elem;
    }

    public XsdComplexType(XmlElem elem, Xsd parent) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
        this.parent = Optional.ofNullable(parent);
    }

    private Optional<ContentDef> contentDef;
    public Optional<ContentDef> getContentDef() {
        if( contentDef!=null )return contentDef;

        var simpleContent = elem.getChildren().flatMap(n -> XsdSimpleContent.parseList(n,this)).head();
        if (simpleContent.isPresent()) return simpleContent.map(a -> a);

        var complexContent = elem.getChildren().flatMap(n -> XsdComplexContent.parseList(n,this)).head();
        if (complexContent.isPresent()) return complexContent.map(a -> a);

        Optional<ElementsLayout> group = elem.getChildren().flatMap(n -> XsdGroup.parseList(n,this)).head().map(a -> a);
        Optional<ElementsLayout> all = elem.getChildren().flatMap(n -> XsdAll.parseList(n, this)).head().map(a -> a);
        Optional<ElementsLayout> choice = elem.getChildren().flatMap(n -> XsdChoice.parseList(n,this)).head().map(a -> a);
        Optional<ElementsLayout> seq = elem.getChildren().flatMap(n -> XsdSequence.parseList(n,this)).head().map(a -> a);
        Optional<ElementsLayout> elemLayout =
            group.or(()->all)
                .or(()->choice)
                .or(()->seq);

        ImList<XsdAttribute> attr = elem.getChildren().flatMap(n -> XsdAttribute.parseList(n,this));
        ImList<XsdAttributeGroup> attrGroup = elem.getChildren().flatMap(n -> XsdAttributeGroup.parseList(n,this));
        Optional<XsdAnyAttribute> anyAttr = elem.getChildren().flatMap(n -> XsdAnyAttribute.parseList(n,this)).head();

        contentDef = Optional.of(new ElementContent(
            elemLayout,
            attr,
            attrGroup,
            anyAttr
        ));

        return contentDef;
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
