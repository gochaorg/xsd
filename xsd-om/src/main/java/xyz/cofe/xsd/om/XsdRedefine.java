package xyz.cofe.xsd.om;

import xyz.cofe.coll.im.ImList;
import xyz.cofe.coll.im.Result;
import xyz.cofe.xml.XmlAttr;
import xyz.cofe.xml.XmlElem;
import xyz.cofe.xml.XmlNode;

import java.util.Objects;
import java.util.Optional;

/**
The <a href="https://www.w3schools.com/xml/el_redefine.asp">redefine</a> element redefines simple and complex types, groups, and attribute groups from an external schema.

 <pre>
&lt;redefine
 id=ID ?
   Optional. Specifies a unique ID for the element

 schemaLocation=anyURI
   Required. A URI to the location of a schema document

 any attributes ?
   Optional. Specifies any other attributes with non-schema namespace
&gt;

( {@link XsdAnnotation annotation}
| ( {@link XsdSimpleType simpleType}
  | {@link XsdComplexType complexType}
  | {@link XsdGroup group}
  | {@link XsdAttributeGroup attributeGroup}
  )
)*

&lt;/redefine&gt;
 </pre>
 */
public final class XsdRedefine implements Xsd, IDAttribute, XsdAnnotation.AnnotationProperty {
    public static final String Name = "redefine";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public static ImList<XsdRedefine> parseList(XmlNode el, Xsd parent) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.of(new XsdRedefine((XmlElem) el, parent))
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

    public XsdRedefine(XmlElem elem, Xsd parent) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
        this.parent = Optional.ofNullable(parent);
    }

    public Result<BuiltInTypes.AnyURI, String> getSchemaLocation() {
        return Result.from(
            elem().attrib("schemaLocation").map(XmlAttr::getValue).head(),
            ()->"schemaLocation not found"
        ).fmap(BuiltInTypes.AnyURI::parse);
    }

    public ImList<XsdSimpleType> getSimpleTypes(){ return elem().getChildren().fmap(n -> XsdSimpleType.parseList(n,this)); }
    public ImList<XsdComplexType> getComplexTypes(){ return elem().getChildren().fmap(n -> XsdComplexType.parseList(n,this)); }
    public ImList<XsdGroup> getGroups(){ return elem().getChildren().fmap(n -> XsdGroup.parseList(n,this)); }
    public ImList<XsdAttributeGroup> getAttributeGroups(){ return elem().getChildren().fmap(n -> XsdAttributeGroup.parseList(n,this)); }
}
