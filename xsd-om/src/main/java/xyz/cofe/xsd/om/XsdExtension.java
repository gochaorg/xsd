package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;

public final class XsdExtension implements Xsd {
    public static final String Extension = "extension";

    public static boolean isExtension(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Extension);
    }

    public static ImList<XsdExtension> parseList(XmlNode el ){
        if( el==null ) throw new IllegalArgumentException("el==null");
        return isExtension(el)
            ? ImList.first(new XsdExtension((XmlElem) el))
            : ImList.empty();
    }

    public final XmlElem elem;

    public XsdExtension(XmlElem elem) {
        if( elem==null ) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
    }
}
