package xyz.cofe.xsd.om;

import xyz.cofe.coll.im.ImList;
import xyz.cofe.coll.im.Result;
import xyz.cofe.xml.XmlAttr;
import xyz.cofe.xml.XmlElem;
import xyz.cofe.xml.XmlNode;

import java.util.Objects;
import java.util.Optional;

import xyz.cofe.xsd.om.BuiltInTypes.*;

/**
 * The <a href="https://www.w3schools.com/xml/el_element.asp">element</a> element defines an element.
 *
 * <pre>
 * &lt;element
 * id=ID ?
 * name=NCName ?
 * ref=QName ?
 * type=QName ?
 * substitutionGroup=QName ?
 * default=string ?
 * fixed=string ?
 * form=qualified|unqualified ?
 * maxOccurs=nonNegativeInteger|unbounded ?
 * minOccurs=nonNegativeInteger ?
 * nillable=true|false ?
 * abstract=true|false ?
 * block=(#all|list of (extension|restriction)) ?
 * final=(#all|list of (extension|restriction)) ?
 * any attributes ?
 * &gt;
 *
 * {@link XsdAnnotation annotation}?,
 * ({@link XsdSimpleType simpleType}|{@link XsdComplexType complexType})?,
 * ({@link XsdUnique unique}|{@link XsdKey key}|{@link XsdKeyref keyref})*
 *
 * &lt;/element&gt;
 *
 * </pre>
 *
 * <table border="1">
 * <tr style="background-color: #ddd">
 * <td> Attribute </td>
 * <td> Description </td>
 * </tr>
 *
 * <tr>
 * <td>id</td>
 * <td> Optional. Specifies a unique ID for the element
 * </td>
 * </tr>
 *
 * <tr style="background-color: #ddd">
 * <td>name</td>
 * <td> Optional. Specifies a name for the element.
 * This attribute is required if the parent element is the schema element
 * </td>
 * </tr>
 *
 * <tr>
 * <td>ref</td>
 * <td> Optional. Refers to the name of another element.
 * The ref attribute can include a namespace prefix. This attribute cannot be used if the parent element is the schema element
 * </td>
 * </tr>
 *
 * <tr style="background-color: #ddd">
 * <td>type</td>
 * <td> Optional. Specifies either the name of a built-in data type, or the name of a simpleType or complexType element
 * </td>
 * </tr>
 *
 * <tr>
 * <td>substitutionGroup</td>
 * <td>  Optional. Specifies the name of an element that can be substituted with this element.
 * This attribute cannot be used if the parent element is not the schema element
 * </td>
 * </tr>
 *
 * <tr style="background-color: #ddd">
 * <td>default</td>
 * <td> Optional. Specifies a default value for the element (can only be used if the element's content is a simple type or text only)
 * </td>
 * </tr>
 *
 * <tr>
 * <td>fixed</td>
 * <td> Optional.
 * Specifies a fixed value for the element (can only be used if the element's content is a simple type or text only)
 * </td>
 * </tr>
 *
 * <tr style="background-color: #ddd">
 * <td>form</td>
 * <td> Optional.
 * Specifies the form for the element. "unqualified" indicates that
 * this element is not required to be qualified with the namespace prefix.
 * "qualified" indicates that this element must be qualified with the namespace prefix.
 * The default value is the value of the elementFormDefault attribute of the schema element.
 * This attribute cannot be used if the parent element is the schema element
 * </td>
 * </tr>
 *
 * <tr>
 * <td>maxOccurs</td>
 * <td> Optional.
 * Specifies the maximum number of times this element can occur in the parent element.
 * The value can be any number &gt;= 0, or if you want to set no limit on the maximum number,
 * use the value "unbounded". Default value is 1. This attribute cannot be used if the parent element is the schema element
 * </td>
 * </tr>
 *
 * <tr style="background-color: #ddd">
 * <td>minOccurs</td>
 * <td> Optional.
 * Specifies the minimum number of times this element can occur in the parent element.
 * The value can be any number &gt;= 0. Default value is 1.
 * This attribute cannot be used if the parent element is the schema element
 * </td>
 * </tr>
 *
 * <tr>
 * <td> nillable </td>
 * <td> Optional. Specifies whether an explicit null value can be assigned to the element.
 * True enables an instance of the element to have the null attribute set to true.
 * The null attribute is defined as part of the XML Schema namespace for instances. Default is false
 * </td>
 * </tr>
 *
 * <tr style="background-color: #ddd">
 * <td>abstract</td>
 * <td> Optional. Specifies whether the element can be used in an instance document.
 * True indicates that the element cannot appear in the instance document.
 * Instead, another element whose substitutionGroup attribute contains the qualified name
 * (QName) of this element must appear in this element's place. Default is false
 * </td>
 * </tr>
 *
 * <tr>
 * <td>block</td><td>
 * Optional.
 * Prevents an element with a specified type of derivation from being used in place of this element.
 * This value can contain #all or a list that is a subset of extension, restriction, or equivClass:
 * <br/> extension -        prevents elements derived by extension
 * <br/> restriction -      prevents elements derived by restriction
 * <br/> substitution -     prevents elements derived by substitution
 * <br/> #all - prevents all derived elements
 * </td>
 * </tr>
 *
 * <tr style="background-color: #ddd">
 * <td>
 * final
 * </td><td>
 * Optional. Sets the default value of the final attribute on the element element.  This attribute cannot be used if the parent element is not the schema element. This value can contain #all or a list that is a subset of extension or restriction:
 * <br/>   extension - prevents elements derived by extension
 * <br/>   restriction - prevents elements derived by restriction
 * <br/>   #all - prevents all derived elements
 * </td>
 * </tr>
 *
 * <tr>
 * <td>
 * any attributes
 * </td><td>
 * Optional. Specifies any other attributes with non-schema namespace
 * </td>
 * </tr>
 *
 * </table>
 */
@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "OptionalAssignedToNull"})
public final class XsdElement implements Xsd,
                                         IDAttribute,
                                         XsdAnnotation.AnnotationProperty,
                                         ElementsLayout,
                                         MinOccursAttribute,
                                         MaxOccursAttribute,
                                         NamespaceAttribute {
    public static final String Name = "element";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public static ImList<XsdElement> parseList(XmlNode el, Xsd parent) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.of(new XsdElement((XmlElem) el, parent))
            : ImList.of();
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

    public XsdElement(XmlElem elem, Xsd parent) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
        this.parent = Optional.ofNullable(parent);
    }

    //region uniques : ImList<XsdUnique>
    private ImList<XsdUnique> uniques;

    public ImList<XsdUnique> getUniques() {
        if (uniques != null) return uniques;
        uniques = elem.getChildren().fmap(n -> XsdUnique.parseList(n, this));
        return uniques;
    }

    //endregion
    //region keys : ImList<XsdKey>
    private ImList<XsdKey> keys;

    public ImList<XsdKey> getKeys() {
        if (keys != null) return keys;
        keys = elem.getChildren().fmap(n -> XsdKey.parseList(n, this));
        return keys;
    }

    //endregion
    //region keyrefs : ImList<XsdKeyref>
    private ImList<XsdKeyref> keyrefs;

    public ImList<XsdKeyref> getKeyrefs() {
        if (keyrefs != null) return keyrefs;
        keyrefs = elem.getChildren().fmap(n -> XsdKeyref.parseList(n, this));
        return keyrefs;
    }

    //endregion
    //region typeDef : Optional<TypeDef>
    private Optional<TypeDef> typeDef;

    public Optional<TypeDef> getTypeDef() {
        if (typeDef != null) return typeDef;
        Optional<TypeDef> simple = elem.getChildren().fmap(n -> XsdSimpleType.parseList(n, this)).head().map(a -> (TypeDef) a);
        Optional<TypeDef> complex = elem.getChildren().fmap(n -> XsdComplexType.parseList(n, this)).head().map(a -> (TypeDef) a);
        typeDef = simple.or(() -> complex);
        return typeDef;
    }

    //endregion
    //region name : Result<NCNAME, String>
    public Result<NCNAME, String> getName() {
        return Result.from(
            elem.attrib("name").map(XmlAttr::getValue).head(),
            () -> "name not found"
        ).fmap(NCNAME::parse);
    }

    //endregion
    //region ref : Result<QName, String>
    public Result<QName, String> getRef() {
        return Result.from(
            elem.attrib("ref").map(XmlAttr::getValue).head(),
            () -> "ref"
        ).fmap(QName::parse);
    }

    //endregion
    //region type : Result<QName, String>
    public Result<QName, String> getType() {
        return Result.from(
                elem.attrib("type").map(XmlAttr::getValue).head(),
                () -> "type"
            )
            .fmap(QName::parse);
    }

    //endregion
    //region substitutionGroup : Result<QName, String>
    public Result<QName, String> getSubstitutionGroup() {
        return Result.from(
            elem.attrib("substitutionGroup").map(XmlAttr::getValue).head(),
            () -> "substitutionGroup"
        ).fmap(QName::parse);
    }

    //endregion
    //region default : Result<String, String>
    public Result<String, String> getDefault() {
        return Result.from(
            elem.attrib("default").map(XmlAttr::getValue).head(),
            () -> "default");
    }

    //endregion
    //region fixed : Result<String, String>
    public Result<String, String> getFixed() {
        return Result.from(
            elem.attrib("fixed").map(XmlAttr::getValue).head(),
            () -> "fixed")
            ;
    }

    //endregion
    //region nillable : Result<BOOLEAN, String>
    public Result<BOOLEAN, String> getNillable() {
        return Result.from(
                elem.attrib("nillable").map(XmlAttr::getValue).head(),
                () -> "nillable")
            .fmap(BOOLEAN::parse);
    }

    //endregion
    //region abstract : Result<String, String>
    public Result<String, String> getAbstract() {
        return Result.from(
            elem.attrib("abstract").map(XmlAttr::getValue).head(),
            () -> "abstract");
    }

    //endregion
    //region block : Result<String, String>
    public Result<String, String> getBlock() {
        return Result.from(elem.attrib("block").map(XmlAttr::getValue).head(),
            () -> "block");
    }

    //endregion
    //region final : Result<String, String>
    public Result<String, String> getFinal() {
        return Result.from(elem.attrib("final").map(XmlAttr::getValue).head(),
            () -> "final");
    }
    //endregion

    private Result<ImList<TypeDef>, String> refTypes;

    public Result<ImList<TypeDef>, String> getRefTypes() {
        if (refTypes != null) return refTypes;
        refTypes = getType().fmap(typeQName -> TypeDef.resolveTypeDefs(typeQName, this));
        return refTypes;
    }

    public Result<TypeDef, String> getRefType() {
        return getRefTypes().fmap(types -> types.size() == 1 ?
            Result.from(
                types.head(),
                () -> "expect one ref types"
            ) : Result.error("expect one ref types"));
    }
}
