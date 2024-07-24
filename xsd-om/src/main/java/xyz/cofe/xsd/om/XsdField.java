package xyz.cofe.xsd.om;

import xyz.cofe.coll.im.ImList;
import xyz.cofe.coll.im.Result;
import xyz.cofe.xml.XmlAttr;
import xyz.cofe.xml.XmlElem;
import xyz.cofe.xml.XmlNode;

import java.util.Objects;
import java.util.Optional;

/**
 The <a href="https://www.w3schools.com/xml/el_field.asp">field</a> element specifies an XPath expression that
 specifies the value used to define an identity constraint.

 <pre>
&lt;field
 id=ID ?
   Optional. Specifies a unique ID for the element

 xpath=XPath expression
   Required. Identifies a single element or attribute whose content or value is used for the constraint

 any attributes ?
   Optional. Specifies any other attributes with non-schema namespace
&gt;

({@link XsdAnnotation annotation}?)

&lt;/field&gt;
 </pre>
 */
public final class XsdField implements Xsd,
                                       XsdAnnotation.AnnotationProperty,
                                       IDAttribute {
    public static final String Name = "field";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public static ImList<XsdField> parseList(XmlNode el, Xsd parent) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.of(new XsdField((XmlElem) el, parent))
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

    public XsdField(XmlElem elem, Xsd parent) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
        this.parent = Optional.ofNullable(parent);
    }

    public Result<BuiltInTypes.ID, String> getId() {
        return Result.from(
            elem.attrib("id").map(XmlAttr::getValue).head(),
            ()->"id not found"
        ).fmap(BuiltInTypes.ID::parse);
    }

    public Result<String, String> getXPath() {
        return Result.from(
            elem.attrib("xpath").map(XmlAttr::getValue).head(),
            ()->"xpath not found"
        );
    }
}
