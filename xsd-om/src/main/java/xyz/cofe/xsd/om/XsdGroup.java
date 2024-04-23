package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;
import java.util.Optional;

/*
https://www.w3schools.com/xml/el_group.asp

<group
id=ID
name=NCName
ref=QName
maxOccurs=nonNegativeInteger|unbounded
minOccurs=nonNegativeInteger
any attributes
>

(annotation?,(all|choice|sequence)?)

</group>
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
