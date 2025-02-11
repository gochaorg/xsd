package xyz.cofe.xsd.om;

import xyz.cofe.coll.im.ImList;
import xyz.cofe.coll.im.Result;
import xyz.cofe.xml.XmlAttr;
import xyz.cofe.xml.XmlElem;
import xyz.cofe.xml.XmlNode;

import java.util.Objects;
import java.util.Optional;

/**
The <a href="https://www.w3schools.com/xml/el_notation.asp">notation</a> element describes the format of non-XML data within an XML document.

 <pre>
&lt;notation
 id=ID ?
   Optional. Specifies a unique ID for the element

 name=NCName
   Required. Specifies a name for the element

 public=anyURI
   Required. Specifies a URI corresponding to the public identifier

 system=anyURI ?
   Optional. Specifies a URI corresponding to the system identifier

 any attributes ?
   Optional. Specifies any other attributes with non-schema namespace
&gt;

({@link XsdAnnotation annotation}?)

&lt;/notation&gt;
 </pre>
 */
public final class XsdNotation implements Xsd, IDAttribute, NamespaceAttribute, XsdAnnotation.AnnotationProperty {
    public static final String Name = "notation";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public static ImList<XsdNotation> parseList(XmlNode el, Xsd parent) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.of(new XsdNotation((XmlElem) el, parent))
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

    public XsdNotation(XmlElem elem, Xsd parent) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
        this.parent = Optional.ofNullable(parent);
    }

    public Result<BuiltInTypes.AnyURI, String> getPublic() {
        return Result.from(
            elem().attrib("public").map(XmlAttr::getValue).head(),
            ()->"public not found"
        ).fmap(BuiltInTypes.AnyURI::parse);
    }

    public Result<BuiltInTypes.AnyURI, String> getSystem() {
        return Result.from(
            elem().attrib("system").map(XmlAttr::getValue).head(),
            ()->"system not found"
        ).fmap(BuiltInTypes.AnyURI::parse);
    }
}
