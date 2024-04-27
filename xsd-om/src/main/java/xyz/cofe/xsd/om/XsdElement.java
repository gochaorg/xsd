package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.Either;
import xyz.cofe.im.struct.ImList;
import xyz.cofe.im.struct.Result;
import xyz.cofe.xsd.om.xml.XmlAttr;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;
import java.util.Optional;

import xyz.cofe.xsd.om.BuiltInTypes.*;

/**
 The <a href="https://www.w3schools.com/xml/el_element.asp">element</a> element defines an element.

 <pre>
&lt;element
 id=ID ?
 name=NCName ?
 ref=QName ?
 type=QName ?
 substitutionGroup=QName ?
 default=string ?
 fixed=string ?
 form=qualified|unqualified ?
 maxOccurs=nonNegativeInteger|unbounded ?
 minOccurs=nonNegativeInteger ?
 nillable=true|false ?
 abstract=true|false ?
 block=(#all|list of (extension|restriction)) ?
 final=(#all|list of (extension|restriction)) ?
 any attributes ?
&gt;

 {@link XsdAnnotation annotation}?,
 ({@link XsdSimpleType simpleType}|{@link XsdComplexType complexType})?,
 ({@link XsdUnique unique}|{@link XsdKey key}|{@link XsdKeyref keyref})*

&lt;/element&gt;

 </pre>

<table border="1">
 <tr style="background-color: #ddd">
    <td> Attribute </td>
    <td> Description </td>
 </tr>

 <tr>
    <td>id</td>
    <td> Optional. Specifies a unique ID for the element
    </td>
 </tr>

 <tr style="background-color: #ddd">
    <td>name</td>
    <td> Optional. Specifies a name for the element.
    This attribute is required if the parent element is the schema element
    </td>
 </tr>

 <tr>
    <td>ref</td>
    <td> Optional. Refers to the name of another element.
    The ref attribute can include a namespace prefix. This attribute cannot be used if the parent element is the schema element
    </td>
 </tr>

 <tr style="background-color: #ddd">
    <td>type</td>
    <td> Optional. Specifies either the name of a built-in data type, or the name of a simpleType or complexType element
    </td>
 </tr>

 <tr>
    <td>substitutionGroup</td>
    <td>  Optional. Specifies the name of an element that can be substituted with this element.
    This attribute cannot be used if the parent element is not the schema element
    </td>
 </tr>

 <tr style="background-color: #ddd">
    <td>default</td>
    <td> Optional. Specifies a default value for the element (can only be used if the element's content is a simple type or text only)
    </td>
 </tr>

 <tr>
    <td>fixed</td>
    <td> Optional.
    Specifies a fixed value for the element (can only be used if the element's content is a simple type or text only)
    </td>
 </tr>

 <tr style="background-color: #ddd">
    <td>form</td>
    <td> Optional.
    Specifies the form for the element. "unqualified" indicates that
    this element is not required to be qualified with the namespace prefix.
    "qualified" indicates that this element must be qualified with the namespace prefix.
    The default value is the value of the elementFormDefault attribute of the schema element.
    This attribute cannot be used if the parent element is the schema element
    </td>
 </tr>

 <tr>
    <td>maxOccurs</td>
    <td> Optional.
    Specifies the maximum number of times this element can occur in the parent element.
    The value can be any number &gt;= 0, or if you want to set no limit on the maximum number,
    use the value "unbounded". Default value is 1. This attribute cannot be used if the parent element is the schema element
    </td>
 </tr>

 <tr style="background-color: #ddd">
    <td>minOccurs</td>
    <td> Optional.
    Specifies the minimum number of times this element can occur in the parent element.
    The value can be any number &gt;= 0. Default value is 1.
    This attribute cannot be used if the parent element is the schema element
    </td>
 </tr>

 <tr>
    <td> nillable </td>
    <td> Optional. Specifies whether an explicit null value can be assigned to the element.
    True enables an instance of the element to have the null attribute set to true.
    The null attribute is defined as part of the XML Schema namespace for instances. Default is false
    </td>
 </tr>

 <tr style="background-color: #ddd">
    <td>abstract</td>
    <td> Optional. Specifies whether the element can be used in an instance document.
    True indicates that the element cannot appear in the instance document.
 Instead, another element whose substitutionGroup attribute contains the qualified name
  (QName) of this element must appear in this element's place. Default is false
   </td>
 </tr>

 <tr>
 <td>block</td><td>
    Optional.
    Prevents an element with a specified type of derivation from being used in place of this element.
    This value can contain #all or a list that is a subset of extension, restriction, or equivClass:
    <br/> extension -        prevents elements derived by extension
    <br/> restriction -      prevents elements derived by restriction
    <br/> substitution -     prevents elements derived by substitution
    <br/> #all - prevents all derived elements
    </td>
 </tr>

 <tr style="background-color: #ddd">
 <td>
final
 </td><td>
   Optional. Sets the default value of the final attribute on the element element.  This attribute cannot be used if the parent element is not the schema element. This value can contain #all or a list that is a subset of extension or restriction:
   <br/>   extension - prevents elements derived by extension
   <br/>   restriction - prevents elements derived by restriction
   <br/>   #all - prevents all derived elements
 </td>
 </tr>

 <tr>
 <td>
any attributes
 </td><td>
 Optional. Specifies any other attributes with non-schema namespace
 </td>
 </tr>

 </table>
 */
public final class XsdElement implements Xsd,
                                         IDAttribute,
                                         XsdAnnotation.AnnotationProperty,
                                         ElementsLayout {
    public static final String Name = "element";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public static ImList<XsdElement> parseList(XmlNode el,Xsd parent) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.first(new XsdElement((XmlElem) el, parent))
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

    public XsdElement(XmlElem elem,Xsd parent) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
        this.parent = Optional.ofNullable(parent);
    }

    public ImList<XsdUnique> getUniques() {
        return elem.getChildren().flatMap(n -> XsdUnique.parseList(n, this));
    }

    public ImList<XsdKey> getKeys() {
        return elem.getChildren().flatMap(n -> XsdKey.parseList(n, this));
    }

    public ImList<XsdKeyref> getKeyrefs() {
        return elem.getChildren().flatMap(n -> XsdKeyref.parseList(n,this));
    }

    public Optional<TypeDef> getTypeDef() {
        Optional<TypeDef> simple = elem.getChildren().flatMap(n -> XsdSimpleType.parseList(n, this)).head().map(a -> (TypeDef) a);
        Optional<TypeDef> complex = elem.getChildren().flatMap(n -> XsdComplexType.parseList(n, this)).head().map(a -> (TypeDef) a);
        return simple.or(() -> complex);
    }

    public Result<NCNAME, String> getName() {
        return Result.of(
            elem.attrib("name").map(XmlAttr::getValue).head(),
            "name not found"
        ).flatMap(NCNAME::parse);
    }

    public Result<QName, String> getRef() {
        return Result.of(elem.attrib("ref")
                .map(XmlAttr::getValue).head(), "ref")
            .flatMap(QName::parse);
    }

    public Result<QName, String> getType() {
        return Result.of(elem.attrib("type")
                .map(XmlAttr::getValue).head(), "type")
            .flatMap(QName::parse);
    }

    public Result<QName, String> getSubstitutionGroup() {
        return Result.of(elem.attrib("substitutionGroup")
                .map(XmlAttr::getValue).head(), "substitutionGroup")
            .flatMap(QName::parse);
    }

    public Result<String, String> getDefault() {
        return Result.of(elem.attrib("default")
            .map(XmlAttr::getValue).head(), "default");
    }

    public Result<String, String> getFixed() {
        return Result.of(elem.attrib("fixed")
            .map(XmlAttr::getValue).head(), "fixed");
    }

    public Result<Either<NON_NEGATIVE_INTEGER, Unbounded>, String> getMaxOccurs() {
        return Result.of(elem.attrib("maxOccurs")
                .map(XmlAttr::getValue).head(), "maxOccurs")
            .flatMap(s -> {
                if (s.equalsIgnoreCase("unbounded"))
                    return Result.ok(Either.right(Unbounded.instance));
                return NON_NEGATIVE_INTEGER.parse(s).map(Either::left);
            });
    }

    public Result<NON_NEGATIVE_INTEGER, String> getMinOccurs() {
        return Result.of(elem.attrib("minOccurs")
                .map(XmlAttr::getValue).head(), "minOccurs")
            .flatMap(NON_NEGATIVE_INTEGER::parse);
    }

    public Result<BOOLEAN, String> getNillable() {
        return Result.of(elem.attrib("nillable")
                .map(XmlAttr::getValue).head(), "nillable")
            .flatMap(BOOLEAN::parse);
    }

    public Result<String, String> getAbstract() {
        return Result.of(elem.attrib("abstract")
            .map(XmlAttr::getValue).head(), "abstract");
    }

    public Result<String, String> getBlock() {
        return Result.of(elem.attrib("block")
            .map(XmlAttr::getValue).head(), "block");
    }

    public Result<String, String> getFinal() {
        return Result.of(elem.attrib("final")
            .map(XmlAttr::getValue).head(), "final");
    }
}
