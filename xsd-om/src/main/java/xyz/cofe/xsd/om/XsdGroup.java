package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.xml.XmlElem;
import xyz.cofe.xml.XmlNode;

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
@SuppressWarnings({"OptionalAssignedToNull", "OptionalUsedAsFieldOrParameterType"})
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

    public static ImList<XsdGroup> parseList(XmlNode el, Xsd parent) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.first(new XsdGroup((XmlElem) el, parent))
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

    public XsdGroup(XmlElem elem, Xsd parent) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
        this.parent = Optional.ofNullable(parent);
    }

    public sealed interface Nested permits XsdAll,
                                           XsdChoice,
                                           XsdSequence {
        public static ImList<Nested> parseList(XmlNode node, Xsd parent) {
            if (node == null) throw new IllegalArgumentException("node==null");
            ImList<Nested> r1 = XsdAll.parseList(node, parent).map(a -> a);
            ImList<Nested> r2 = XsdChoice.parseList(node, parent).map(a -> a);
            ImList<Nested> r3 = XsdSequence.parseList(node, parent).map(a -> a);
            return r1.join(r2).join(r3);
        }
    }

    private Optional<Nested> nested;
    public Optional<Nested> getNested() {
        if( nested!=null )return nested;
        nested = elem().getChildren().flatMap(n -> Nested.parseList(n,this)).head();
        return nested;
    }
}
