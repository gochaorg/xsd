package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.im.struct.Result;
import xyz.cofe.xsd.om.xml.XmlAttr;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;

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

    public static ImList<XsdRedefine> parseList(XmlNode el) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.first(new XsdRedefine((XmlElem) el))
            : ImList.empty();
    }

    public final XmlElem elem;

    @Override
    public XmlElem elem() {
        return elem;
    }

    public XsdRedefine(XmlElem elem) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
    }

    public Result<BuiltInTypes.AnyURI, String> getSchemaLocation() {
        return Result.of(
            elem().attrib("schemaLocation").map(XmlAttr::getValue).head(),
            "schemaLocation not found"
        ).flatMap(BuiltInTypes.AnyURI::parse);
    }

    public ImList<XsdSimpleType> getSimpleTypes(){ return elem().getChildren().flatMap(XsdSimpleType::parseList); }
    public ImList<XsdComplexType> getComplexTypes(){ return elem().getChildren().flatMap(XsdComplexType::parseList); }
    public ImList<XsdGroup> getGroups(){ return elem().getChildren().flatMap(XsdGroup::parseList); }
    public ImList<XsdAttributeGroup> getAttributeGroups(){ return elem().getChildren().flatMap(XsdAttributeGroup::parseList); }
}
