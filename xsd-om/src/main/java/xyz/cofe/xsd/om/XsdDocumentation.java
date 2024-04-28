package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.xml.XmlElem;
import xyz.cofe.xml.XmlNode;

import java.util.Objects;
import java.util.Optional;

/*
https://www.w3schools.com/xml/el_documentation.asp

<documentation
source=URI reference
xml:lang=language>

Any well-formed XML content

</documentation>
 */
public final class XsdDocumentation implements Xsd {
    public static final String Name = "documentation";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
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

    public XsdDocumentation(XmlElem elem, Xsd parent) {
        if( elem==null ) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
        this.parent = Optional.ofNullable(parent);
    }

    public static ImList<XsdDocumentation> parseList( XmlNode el, Xsd parent ){
        if( el==null ) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.first(new XsdDocumentation((XmlElem) el, parent))
            : ImList.empty();
    }

    public String getText(){
        return elem.getTextContent();
    }

}
