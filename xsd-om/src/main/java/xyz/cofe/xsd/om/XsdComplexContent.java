package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.im.struct.Result;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;

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
    public static final String Name = "complexType";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public static ImList<XsdComplexContent> parseList(XmlNode el) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.first(new XsdComplexContent((XmlElem) el))
            : ImList.empty();
    }

    public final XmlElem elem;

    @Override
    public XmlElem elem() {
        return elem;
    }

    public XsdComplexContent(XmlElem elem) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
    }

    public sealed interface Nested permits XsdExtension,
                                           XsdRestriction {
        public static ImList<Nested> parseList(XmlNode node){
            if( node==null ) throw new IllegalArgumentException("node==null");
            ImList<Nested> r1 = XsdExtension.parseList(node).map(a -> a);
            ImList<Nested> r2 = XsdRestriction.parseList(node).map(a -> a);
            return r1.join(r2);
        }
    }

    public Result<Nested, String> getNested(){
        return Result.of( elem().getChildren().flatMap(Nested::parseList).head(), "not found nested restriction|extension" );
    }
}
