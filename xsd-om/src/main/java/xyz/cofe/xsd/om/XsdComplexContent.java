package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;

public final class XsdComplexContent implements Xsd, ContentDef {
    public static final String ComplexType = "complexType";

    public static boolean isComplexType(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), ComplexType);
    }

    public static ImList<XsdComplexContent> parseList(XmlNode el ){
        if( el==null ) throw new IllegalArgumentException("el==null");
        return isComplexType(el)
            ? ImList.first(new XsdComplexContent((XmlElem) el))
            : ImList.empty();
    }

    public final XmlElem elem;

    public XsdComplexContent(XmlElem elem) {
        if( elem==null ) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
    }

}
