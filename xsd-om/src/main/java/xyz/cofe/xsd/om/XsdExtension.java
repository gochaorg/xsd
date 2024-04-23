package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;

/*
https://www.w3schools.com/xml/el_extension.asp

<extension
id=ID
base=QName
any attributes
>

(annotation?,((group|all|choice|sequence)?,
((attribute|attributeGroup)*,anyAttribute?)))

</extension>
 */
public final class XsdExtension implements Xsd {
    public static final String Name = "extension";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public static ImList<XsdExtension> parseList(XmlNode el ){
        if( el==null ) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.first(new XsdExtension((XmlElem) el))
            : ImList.empty();
    }

    public final XmlElem elem;

    public XsdExtension(XmlElem elem) {
        if( elem==null ) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
    }
}
