package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.xml.XmlElem;
import xyz.cofe.xml.XmlNode;

import java.util.Objects;
import java.util.Optional;

/**
 * The <a href="https://www.w3schools.com/xml/el_all.asp">all</a> element specifies that the child elements
 * can appear in <b>any order</b> and that each child element can occur zero or one time.
 *
 * <pre>
 * &lt;all
 * id=ID?
 * maxOccurs=1?    <i>The value must be 1.</i>
 * minOccurs=0|1?  <i>The value can be 0 or 1. Default value is 1</i>
 * any attributes?
 * &gt;
 *
 * ({@link XsdAnnotation annotation}?,{@link XsdElement element}*)
 *
 * &lt;/all&gt;
 * </pre>
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class XsdAll implements Xsd,
                                     ElementsLayout,
                                     IDAttribute,
                                     XsdGroup.Nested,
                                     XsdExtension.NestedEl,
                                     MinOccursAttribute,
                                     MaxOccursAttribute {
    public static final String Name = "all";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public static ImList<XsdAll> parseList(XmlNode el, Xsd parent) {
        if (el == null) throw new IllegalArgumentException("el==null");
        if( parent==null ) throw new IllegalArgumentException("parent==null");
        return isMatch(el)
            ? ImList.first(new XsdAll((XmlElem) el, parent))
            : ImList.empty();
    }

    public final XmlElem elem;
    @Override
    public XmlElem elem() {
        return elem;
    }

    public final Optional<Xsd> parent;

    @Override
    public Optional<Xsd> getParent() {
        return parent;
    }

    public XsdAll(XmlElem elem, Xsd parent) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;

        if( parent==null ) throw new IllegalArgumentException("parent==null");
        this.parent = Optional.of(parent);
    }

    public ImList<XsdAppinfo> getAppinfos() {return elem.getChildren().flatMap(n -> XsdAppinfo.parseList(n,this));}

    public ImList<XsdDocumentation> getDocumentations() {return elem.getChildren().flatMap(n -> XsdDocumentation.parseList(n,this));}
}
