package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;

public final class XsdAppinfo implements Xsd {
    public static final String Appinfo = "appinfo";

    public static boolean isAttribute(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Appinfo);
    }

    public static ImList<XsdAppinfo> parseList(XmlNode el ){
        if( el==null ) throw new IllegalArgumentException("el==null");
        return isAttribute(el)
            ? ImList.first(new XsdAppinfo((XmlElem) el))
            : ImList.empty();
    }

    public final XmlElem elem;

    public XsdAppinfo(XmlElem elem) {
        if( elem==null ) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
    }
}
