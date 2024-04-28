package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.xml.XmlElem;
import xyz.cofe.xml.XmlNode;

import java.util.Objects;
import java.util.Optional;

/**
 The <a href="https://www.w3schools.com/xml/el_annotation.asp">annotation</a> element is a top level element that specifies schema comments. The comments serve as inline documentation.

 <pre>
&lt;annotation
  id=ID?
  any attributes?
&gt;

({@link XsdAppinfo appinfo}|{@link XsdDocumentation documentation})*

&lt;/annotation&gt;
 </pre>
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class XsdAnnotation implements Xsd,
                                            IDAttribute {
    public non-sealed interface AnnotationProperty extends Xsd {
        public default ImList<XsdAnnotation> getAnnotations() {
            return elem().getChildren().flatMap(n->XsdAnnotation.parseList(n,this));
        }
    }

    public static ImList<XsdAnnotation> parseList(XmlNode el, Xsd parent) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.first(new XsdAnnotation((XmlElem) el, parent))
            : ImList.empty();
    }

    public static final String Name = "annotation";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public final XmlElem elem;
    public final Optional<Xsd> parent;

    @Override
    public Optional<Xsd> getParent() {
        return parent;
    }

    @Override
    public XmlElem elem() {
        return elem;
    }

    public XsdAnnotation(XmlElem elem) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
        this.parent = Optional.empty();
    }

    public XsdAnnotation(XmlElem elem, Xsd parent) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
        this.parent = Optional.ofNullable(parent);
    }

    public ImList<XsdDocumentation> getDocumentations() {
        return elem.getChildren().flatMap(n -> XsdDocumentation.parseList(n,this));
    }
}
