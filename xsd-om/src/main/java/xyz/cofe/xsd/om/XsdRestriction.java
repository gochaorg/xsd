package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;

public final class XsdRestriction implements Xsd {
    public static final String Restriction = "restriction";

    public static boolean isAttribute(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), XsdConst.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Restriction);
    }

    public static ImList<XsdRestriction> parseList(XmlNode el) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isAttribute(el)
            ? ImList.first(new XsdRestriction((XmlElem) el))
            : ImList.empty();
    }

    public final XmlElem elem;

    public XsdRestriction(XmlElem elem) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
    }
}
