package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.im.struct.Result;
import xyz.cofe.xsd.om.xml.XmlAttr;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;
import java.util.Optional;

/**
 The <a href="https://www.w3schools.com/xml/el_simpletype.asp">simpleType</a> element defines a simple type and specifies the constraints and information about the values of attributes or text-only elements.

 <pre>
&lt;simpleType
 id=ID ?
   Optional. Specifies a unique ID for the element

 name=NCName
   Specifies a name for the element.
   This attribute is required if the simpleType element is
   a child of the schema element, otherwise it is not allowed

 any attributes ?
   Optional. Specifies any other attributes with non-schema namespace
&gt;

( {@link XsdAnnotation annotation}?,
  ({@link XsdRestriction restriction}|{@link XsdList list}|{@link XsdUnion union})
)

&lt;/simpleType&gt;
 </pre>
 
 <h2>examples</h2>
 
 <pre>

 &lt;xs:element name="age"&gt;
     &lt;xs:simpleType&gt;
         &lt;xs:restriction base="xs:integer"&gt;
             &lt;xs:minInclusive value="0"/&gt;
             &lt;xs:maxInclusive value="100"/&gt;
         &lt;/xs:restriction&gt;
     &lt;/xs:simpleType&gt;
 &lt;/xs:element&gt; 
 
 </pre>
 */
public final class XsdSimpleType implements Xsd, TypeDef, IDAttribute, NameAttribute, XsdAnnotation.AnnotationProperty {
    public static final String Name = "simpleType";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public static ImList<XsdSimpleType> parseList(XmlNode el, Xsd parent) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.first(new XsdSimpleType((XmlElem) el, parent))
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

    public XsdSimpleType(XmlElem elem, Xsd parent) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
        this.parent = Optional.ofNullable(parent);
    }

    public Optional<SimpleTypeContent> getRestriction() {
        Optional<SimpleTypeContent> c1 = elem.getChildren().flatMap(n -> XsdRestriction.parseList(n,this)).head().map( a->a );
        Optional<SimpleTypeContent> c2 = elem.getChildren().flatMap(n -> XsdList.parseList(n,this)).head().map( a->a );
        Optional<SimpleTypeContent> c3 = elem.getChildren().flatMap(n -> XsdUnion.parseList(n,this)).head().map( a->a );
        return c1.or(()->c2).or(()->c3);
    }
}
