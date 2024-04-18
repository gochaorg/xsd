package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;

/*
https://www.w3schools.com/xml/el_all.asp

<annotation
id=ID
any attributes
>

(appinfo|documentation)*

</annotation>
*/
public final class XsdAll implements Xsd,
                                     ElementsLayout {
    public static final String All = "all";

    public static boolean isAttribute(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), All);
    }

    public static ImList<XsdAll> parseList(XmlNode el ){
        if( el==null ) throw new IllegalArgumentException("el==null");
        return isAttribute(el)
            ? ImList.first(new XsdAll((XmlElem) el))
            : ImList.empty();
    }

    public final XmlElem elem;

    public XsdAll(XmlElem elem) {
        if( elem==null ) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
    }
}
