package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;
import java.util.Optional;

/**
 The <a href="https://www.w3schools.com/xml/el_sequence.asp">sequence</a> element specifies that the child elements must appear in a sequence. Each child element can occur from 0 to any number of times.

 <pre>
&lt;sequence
 id=ID ?
   Optional. Specifies a unique ID for the element

 maxOccurs=nonNegativeInteger|unbounded ?
   Optional. Specifies the maximum number of times
   the sequence element can occur in the parent element.
   The value can be any number &gt;= 0,
   or if you want to set no limit on the maximum number,
   use the value "unbounded".
   Default value is 1

 minOccurs=nonNegativeInteger ?
   Optional.
   Specifies the minimum number of times
   the sequence element can occur in the parent element.
   The value can be any number &gt;= 0.
   Default value is 1

 any attributes ?
   Optional.
   Specifies any other attributes with non-schema namespace
&gt;

( {@link XsdAnnotation annotation}?,
  ({@link XsdElement element}|{@link XsdGroup group}|{@link XsdChoice choice}|{@link XsdSequence sequence}|{@link XsdAny any})*
)

&lt;/sequence&gt;
 </pre>

 <h2>examples</h2>

 <pre>

 &lt;xs:element name="personinfo"&gt;
     &lt;xs:complexType&gt;
         &lt;xs:sequence&gt;
             &lt;xs:element name="firstname" type="xs:string"/&gt;
             &lt;xs:element name="lastname" type="xs:string"/&gt;
             &lt;xs:element name="address" type="xs:string"/&gt;
             &lt;xs:element name="city" type="xs:string"/&gt;
             &lt;xs:element name="country" type="xs:string"/&gt;
         &lt;/xs:sequence&gt;
     &lt;/xs:complexType&gt;
 &lt;/xs:element&gt;

 &lt;xs:element name="pets"&gt;
     &lt;xs:complexType&gt;
         &lt;xs:sequence minOccurs="0" maxOccurs="unbounded"&gt;
             &lt;xs:element name="dog" type="xs:string"/&gt;
             &lt;xs:element name="cat" type="xs:string"/&gt;
         &lt;/xs:sequence&gt;
     &lt;/xs:complexType&gt;
 &lt;/xs:element&gt; 
 
 </pre>
 */
public final class XsdSequence implements Xsd,
                                          IDAttribute,
                                          ElementsLayout,
                                          XsdAnnotation.AnnotationProperty,
                                          ElementsLayout.NestedHolder,
                                          XsdGroup.Nested,
                                          XsdExtension.NestedEl {
    public static final String Name = "sequence";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public static ImList<XsdSequence> parseList(XmlNode el, Xsd parent) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.first(new XsdSequence((XmlElem) el, parent))
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

    public XsdSequence(XmlElem elem, Xsd parent) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
        this.parent = Optional.ofNullable(parent);
    }
}
