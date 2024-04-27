package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.im.struct.Result;
import xyz.cofe.xsd.om.xml.XmlAttr;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;
import java.util.Optional;

/**
The <a href="https://www.w3schools.com/xml/el_selector.asp">selector</a> element specifies
 an XPath expression that selects a set of elements for an identity constraint (unique, key, and keyref elements).

 <pre>
&lt;selector
 id=ID ?
   Optional. Specifies a unique ID for the element

 xpath=a subset of XPath expression
   Required. Specifies an XPath expression,
   relative to the element being declared,
   that identifies the child elements to
   which the identity constraint applies

 any attributes ?
   Optional.
   Specifies any other attributes with non-schema namespace
&gt;

({@link XsdAnnotation annotation}?)

&lt;/selector&gt;
 </pre>
 */
public final class XsdSelector implements Xsd,
                                          IDAttribute,
                                          XsdAnnotation.AnnotationProperty {
    public static final String Name = "selector";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public static ImList<XsdSelector> parseList(XmlNode el, Xsd parent) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.first(new XsdSelector((XmlElem) el, parent))
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

    public XsdSelector(XmlElem elem, Xsd parent) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
        this.parent = Optional.of(parent);
    }

    public Result<BuiltInTypes.ID, String> getXPath() {
        return Result.of(
            elem().attrib("xpath").map(XmlAttr::getValue).head(),
            "xpath not found"
        ).flatMap(BuiltInTypes.ID::parse);
    }
}
