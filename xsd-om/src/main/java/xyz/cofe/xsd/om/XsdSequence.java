package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;

public final class XsdSequence implements Xsd {
    public static final String Sequence = "sequence";

    public static boolean isSequence(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), XsdConst.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Sequence);
    }

    public static ImList<XsdSequence> parseList(XmlNode el ){
        if( el==null ) throw new IllegalArgumentException("el==null");
        return isSequence(el)
            ? ImList.first(new XsdSequence((XmlElem) el))
            : ImList.empty();
    }

    public final XmlElem elem;

    public XsdSequence(XmlElem elem) {
        if( elem==null ) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
    }
}
