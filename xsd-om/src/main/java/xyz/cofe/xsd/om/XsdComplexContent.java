package xyz.cofe.xsd.om;

import xyz.cofe.coll.im.ImList;
import xyz.cofe.coll.im.Result;
import xyz.cofe.xml.XmlElem;
import xyz.cofe.xml.XmlNode;

import java.util.Objects;
import java.util.Optional;

/**

 The <a href="https://www.w3schools.com/xml/el_complexcontent.asp">complexType</a>
 element defines a complex type. A complex type element is an XML element that contains other elements and/or attributes.

 <pre>
&lt;complexContent
 id=ID ?
   Optional. Specifies a unique ID for the element

 mixed=true|false ?
   Optional.
   Specifies whether character data is allowed to appear
   between the child elements of this complexType element. Default is false

 any attributes ?
   Optional. Specifies any other attributes with non-schema namespace
&gt;

({@link XsdAnnotation annotation}?,({@link XsdRestriction restriction}|{@link XsdExtension extension}))

&lt;/complexContent&gt;
 </pre>
*/
public final class XsdComplexContent implements Xsd,
                                                ContentDef,
                                                XsdAnnotation.AnnotationProperty {
    public static final String Name = "complexContent";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public static ImList<XsdComplexContent> parseList(XmlNode el,Xsd parent) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.of(new XsdComplexContent((XmlElem) el, parent))
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

    public XsdComplexContent(XmlElem elem, Xsd parent) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
        this.parent = Optional.ofNullable(parent);
    }

    public sealed interface Nested permits XsdExtension,
                                           XsdRestriction {
        public static ImList<Nested> parseList(XmlNode node, Xsd parent){
            if( node==null ) throw new IllegalArgumentException("node==null");
            ImList<Nested> r1 = XsdExtension.parseList(node, parent).map(a -> a);
            ImList<Nested> r2 = XsdRestriction.parseList(node, parent).map(a -> a);
            return r1.append(r2);
        }
    }

    private Result<Nested, String> nested;
    public Result<Nested, String> getNested(){
        if( nested!=null )return nested;
        nested = Result.from(
            elem().getChildren().fmap(n -> Nested.parseList(n,this)).head(),
            ()->"not found nested restriction|extension"
        );
        return nested;
    }
}
