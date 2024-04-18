package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;

public final class XsdDocumentation implements Xsd {
    public static final String Documentation = "documentation";

    public static boolean isDocumentation(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Documentation);
    }

    public final XmlElem elem;

    public XsdDocumentation(XmlElem elem) {
        if( elem==null ) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
    }

    public static ImList<XsdDocumentation> parseList( XmlNode el ){
        if( el==null ) throw new IllegalArgumentException("el==null");
        return isDocumentation(el)
            ? ImList.first(new XsdDocumentation((XmlElem) el))
            : ImList.empty();
    }

    public String getText(){
        return elem.getTextContent();
    }

}
