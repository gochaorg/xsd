package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.im.struct.Result;
import xyz.cofe.xml.XmlElem;
import xyz.cofe.xml.XmlNode;

import java.util.Objects;
import java.util.Optional;

/**
 The <a href="https://www.w3schools.com/xml/el_simpleContent.asp">simpleContent</a> element contains extensions or
 restrictions on a text-only complex type or on a simple type as content and contains no elements.

<pre>
&lt;simpleContent
 id=ID ?
   Optional. Specifies a unique ID for the element

 any attributes ?
   Optional. Specifies any other attributes with non-schema namespace
&gt;

({@link XsdAnnotation annotation}?,({@link XsdRestriction restriction}|{@link XsdExtension extension}))

&lt;/simpleContent&gt;
 </pre>

 */
@SuppressWarnings({"unused", "OptionalUsedAsFieldOrParameterType", "OptionalAssignedToNull"})
public final class XsdSimpleContent implements Xsd,
                                               ContentDef,
                                               IDAttribute {
    public static final String Name = "simpleContent";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public static ImList<XsdSimpleContent> parseList(XmlNode el, Xsd parent) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.first(new XsdSimpleContent((XmlElem) el, parent))
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

    public XsdSimpleContent(XmlElem elem, Xsd parent) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
        this.parent = Optional.of(parent);
    }

    public sealed interface Nested permits XsdRestriction,
                                           XsdExtension {}

    private Result<Nested,String> nested;
    public Result<Nested,String> getNested() {
        if( nested!=null )return nested;
        Optional<Nested> r1 = elem().getChildren().flatMap(n -> XsdRestriction.parseList(n,this)).head().map(a -> a);
        Optional<Nested> r2 = elem().getChildren().flatMap(n -> XsdExtension.parseList(n, this)).head().map(a -> a);
        Optional<Nested> r3 = r1.or(() -> r2);
        nested = Result.of(r3, "nested restriction | extension");
        return nested;
    }
}
