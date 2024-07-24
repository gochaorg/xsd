package xyz.cofe.xsd.om;

import xyz.cofe.coll.im.ImList;
import xyz.cofe.xml.XmlElem;
import xyz.cofe.xml.XmlNode;

import java.util.Objects;
import java.util.Optional;

/**
 * The <a href="https://www.w3schools.com/xml/el_list.asp">list</a> element defines a simple type element as a list of values of a specified data type.
 *
 * <pre>
 * &lt;list
 * id=ID ?
 * Optional. Specifies a unique ID for the element
 *
 * itemType=QName
 * Specifies the name of a built-in data type or
 * simpleType element defined in this or another schema.
 * This attribute is not allowed if the content contains a
 * simpleType element, otherwise it is required
 *
 * any attributes ?
 * Optional. Specifies any other attributes with non-schema namespace
 * &gt;
 *
 * ({@link XsdAnnotation annotation}?,({@link XsdSimpleType simpleType}?))
 *
 * &lt;/list&gt;
 * </pre>
 */
public final class XsdList implements Xsd,
                                      SimpleTypeContent,
                                      IDAttribute,
                                      ItemTypeAttribute,
                                      XsdAnnotation.AnnotationProperty {
    public static final String Name = "list";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public static ImList<XsdList> parseList(XmlNode el, Xsd parent) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.of(new XsdList((XmlElem) el, parent))
            : ImList.of();
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

    public XsdList(XmlElem elem, Xsd parent) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
        this.parent = Optional.ofNullable(parent);
    }

    public Optional<XsdSimpleType> getSimpleType() {
        return elem().getChildren().fmap(n -> XsdSimpleType.parseList(n, this)).head();
    }
}
