package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;
import java.util.Optional;

/**

 The <a href="https://www.w3schools.com/xml/el_extension.asp">extension</a> element extends an existing simpleType or complexType element.

<pre>
&lt;extension
 id=ID ?
   Optional. Specifies a unique ID for the element

 base=QName
   Required. Specifies the name of a built-in data type, a simpleType element, or a complexType element

 any attributes ?
 Optional. Specifies any other attributes with non-schema namespace
&gt;

(
 {@link XsdAnnotation annotation}?,

  ( ({@link XsdGroup group}|{@link XsdAll all}|{@link XsdChoice choice}|{@link XsdSequence sequence})? ,

    ( ({@link XsdAttribute attribute} | {@link XsdAttributeGroup attributeGroup})*,
      {@link XsdAnyAttribute anyAttribute}?
    )
  )
)
 
&lt;/extension&gt;
 </pre>
 */
public final class XsdExtension implements Xsd,
                                           XsdComplexContent.Nested,
                                           IDAttribute,
                                           BaseAttribute,
                                           XsdAnnotation.AnnotationProperty {
    public static final String Name = "extension";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public static ImList<XsdExtension> parseList(XmlNode el) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.first(new XsdExtension((XmlElem) el))
            : ImList.empty();
    }

    public final XmlElem elem;

    @Override
    public XmlElem elem() {
        return elem;
    }

    public XsdExtension(XmlElem elem) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
    }

    public sealed interface NestedEl permits XsdGroup, XsdAll, XsdChoice, XsdSequence {
        public static ImList<NestedEl> parseList(XmlNode node){
            ImList<NestedEl> r1 = XsdGroup.parseList(node).map(a->a);
            ImList<NestedEl> r2 = XsdAll.parseList(node).map(a->a);
            ImList<NestedEl> r3 = XsdChoice.parseList(node).map(a->a);
            ImList<NestedEl> r4 = XsdSequence.parseList(node).map(a->a);
            return r1.join(r2).join(r3).join(r4);
        }
    }

    public Optional<NestedEl> getNested(){
        return elem().getChildren().flatMap(NestedEl::parseList).head();
    }

    public Optional<XsdAnyAttribute> getAnyAttribute(){
        return elem().getChildren().flatMap(XsdAnyAttribute::parseList).head();
    }

    public ImList<XsdAttribute> getAttributes(){
        return elem().getChildren().flatMap(XsdAttribute::parseList);
    }

    public ImList<XsdAttributeGroup> getAttributeGroups(){
        return elem().getChildren().flatMap(XsdAttributeGroup::parseList);
    }
}
