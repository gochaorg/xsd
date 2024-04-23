package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;
import java.util.Optional;

/**
 The <a href="https://www.w3schools.com/xml/el_group.asp">group</a> element is used to define a group of elements to be used in complex type definitions.

 <pre>
&lt;group
 id=ID ?
   Optional. Specifies a unique ID for the element

 name=NCName ?
   Optional.
   Specifies a name for the group. This attribute is used only when the schema element is
 the parent of this group element. Name and ref attributes cannot both be present

 ref=QName ?
   Optional. Refers to the name of another group.
   Name and ref attributes cannot both be present

 maxOccurs=nonNegativeInteger|unbounded ?
   Optional. Specifies the maximum number of times the group element can occur in the parent element.
   The value can be any number >= 0, or
   if you want to set no limit on the maximum number, use the value "unbounded".
   Default value is 1

 minOccurs=nonNegativeInteger ?
   Optional. Specifies the minimum number of times the group element can occur in the parent element.
   The value can be any number >= 0. Default value is 1

 any attributes ?
   Optional. Specifies any other attributes with non-schema namespace
&gt;

( {@link XsdAnnotation annotation}?,
  ({@link XsdAll all}|{@link XsdChoice choice}|{@link XsdSequence sequence})?
)

&lt;/group&gt;
 </pre>
 */
public final class XsdGroup implements Xsd,
                                       ElementsLayout,
                                       IDAttribute,
                                       NameAttribute,
                                       RefAttribute,
                                       MinOccursAttribute,
                                       MaxOccursAttribute,
                                       XsdAnnotation.AnnotationProperty,
                                       XsdExtension.NestedEl {

    public static final String Name = "group";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public static ImList<XsdGroup> parseList(XmlNode el) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.first(new XsdGroup((XmlElem) el))
            : ImList.empty();
    }

    public final XmlElem elem;

    @Override
    public XmlElem elem() {
        return elem;
    }

    public XsdGroup(XmlElem elem) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
    }

    public sealed interface Nested permits XsdAll,
                                           XsdChoice,
                                           XsdSequence {
        public static ImList<Nested> parseList(XmlNode node) {
            if (node == null) throw new IllegalArgumentException("node==null");
            ImList<Nested> r1 = XsdAll.parseList(node).map(a -> a);
            ImList<Nested> r2 = XsdChoice.parseList(node).map(a -> a);
            ImList<Nested> r3 = XsdSequence.parseList(node).map(a -> a);
            return r1.join(r2).join(r3);
        }
    }

    public Optional<Nested> getNested() {return elem().getChildren().flatMap(Nested::parseList).head();}
}
