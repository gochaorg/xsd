package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.xml.XmlElem;
import xyz.cofe.xml.XmlNode;

import java.util.Objects;
import java.util.Optional;

/**
 XML Schema <a href="https://www.w3schools.com/xml/el_choice.asp">choice</a> element allows only one of the elements contained in the <choice> declaration to be present within the containing element.

 <pre>
&lt;choice
 id=ID ?
   Optional.
   Specifies a unique ID for the element

 maxOccurs=nonNegativeInteger|unbounded ?
   Optional.
   Specifies the maximum number of times the choice element can occur in the parent element.
   The value can be any number >= 0, or if you want to
   set no limit on the maximum number, use the value "unbounded". Default value is 1

 minOccurs=nonNegativeInteger ?
   Optional.
   Specifies the minimum number of times the choice element can occur in the parent the element.
   The value can be any number &gt;= 0. Default value is 1

 any attributes ?
   Optional. Specifies any other attributes with non-schema namespace
&gt;

( {@link XsdAnnotation annotation}?,
 ({@link XsdElement element}|{@link XsdGroup group}|{@link XsdChoice choice}|{@link XsdSequence sequence}|{@link XsdAny any})*
)

&lt;/choice&gt;
 </pre>
 */
public final class XsdChoice implements Xsd,
                                        ElementsLayout,
                                        IDAttribute,
                                        MinOccursAttribute,
                                        MaxOccursAttribute,
                                        XsdAnnotation.AnnotationProperty,
                                        ElementsLayout.NestedHolder,
                                        XsdGroup.Nested,
                                        XsdExtension.NestedEl {
    public static final String Name = "choice";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public static ImList<XsdChoice> parseList(XmlNode el,Xsd parent) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.first(new XsdChoice((XmlElem) el, parent))
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

    public XsdChoice(XmlElem elem,Xsd parent) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
        this.parent = Optional.ofNullable(parent);
    }

    private ImList<ElementsLayout> nested;
    @Override
    public ImList<ElementsLayout> getNested() {
        if( nested!=null )return nested;
        nested = NestedHolder.super.getNested();
        return nested;
    }
}
