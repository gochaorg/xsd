package xyz.cofe.xsd.om;

import xyz.cofe.coll.im.ImList;
import xyz.cofe.coll.im.Result;
import xyz.cofe.xml.XmlAttr;
import xyz.cofe.xml.XmlElem;
import xyz.cofe.xml.XmlNode;

import java.util.Objects;
import java.util.Optional;

/**
The <a href="https://www.w3schools.com/xml/el_attribute.asp">attribute</a> element defines an attribute.

 <pre>
&lt;attribute
  default=string ?
     Optional.
     Specifies a default value for the attribute.
     Default and fixed attributes cannot both be present

  fixed=string ?
    Optional.
    Specifies a fixed value for the attribute.
    Default and fixed attributes cannot both be present

  form=qualified|unqualified ?

    Optional. Specifies the form for the attribute.
    The default value is the value of the attributeFormDefault
    attribute of the element containing the attribute.

    Can be set to one of the following:

    "qualified" - indicates that this attribute must be
                  qualified with the namespace prefix and
                  the no-colon-name (NCName) of the attribute

    unqualified - indicates that this attribute is not required
                 to be qualified with the namespace prefix and
                 is matched against the (NCName) of the attribute

  id=ID ?
    Optional. Specifies a unique ID for the element

  name=NCName ?
    Optional.
    Specifies the name of the attribute.
    Name and ref attributes cannot both be present

  ref=QName ?
    Optional. Specifies a reference to a named attribute.
    Name and ref attributes cannot both be present.
    If ref is present, simpleType element, form, and type cannot be present

  type=QName ?
    Optional.
    Specifies a built-in data type or a simple type.
    The type attribute can only be present
    when the content does not contain a simpleType element

  use=optional|prohibited|required ?
    Optional. Specifies how the attribute is used. Can be one of the following values:

    optional - the attribute is optional (this is default)
    prohibited - the attribute cannot be used
    required - the attribute is required

  any attributes ?
&gt;

({@link XsdAnnotation annotation}?,({@link XsdSimpleType simpleType}?))

&lt;/attribute&gt;
 </pre>
 */
@SuppressWarnings("ALL")
public final class XsdAttribute implements Xsd,
                                           IDAttribute,
                                           TypeAttribute,
                                           RefAttribute,
                                           NameAttribute,
                                           XsdAnnotation.AnnotationProperty
{
    public static final String Name = "attribute";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public static ImList<XsdAttribute> parseList(XmlNode el, Xsd parent) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.of(new XsdAttribute((XmlElem) el, parent))
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

    public XsdAttribute(XmlElem elem, Xsd parent) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
        this.parent = Optional.ofNullable(parent);
    }

    public Result<String, String> getDefault() {
        return Result.from(
            elem.attrib("default").map(XmlAttr::getValue).head(),
            ()->"default not found"
        );
    }

    public Result<String, String> getFixed() {
        return Result.from(
            elem.attrib("fixed").map(XmlAttr::getValue).head(),
            ()->"fixed not found"
        );
    }

    public Result<String, String> getForm() {
        return Result.from(
            elem.attrib("form").map(XmlAttr::getValue).head(),
            ()->"form not found"
        );
    }

    public Result<String, String> getUse() {
        return Result.from(
            elem.attrib("use").map(XmlAttr::getValue).head(),
            ()->"use not found"
        );
    }

    private Optional<XsdSimpleType> simpleType;
    public Optional<XsdSimpleType> getSimpleType(){
        if( simpleType!=null )return simpleType;
        simpleType = elem().getChildren().fmap(n -> XsdSimpleType.parseList(n,this)).head();
        return simpleType;
    }
}
