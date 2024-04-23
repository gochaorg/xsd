package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;

/*
https://www.w3schools.com/xml/el_choice.asp

<choice
id=ID
maxOccurs=nonNegativeInteger|unbounded
minOccurs=nonNegativeInteger
any attributes
>

(annotation?,(element|group|choice|sequence|any)*)

</choice>
 */
public final class XsdChoice implements Xsd, ElementsLayout {
    public static final String Name = "choice";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public static ImList<XsdChoice> parseList(XmlNode el ){
        if( el==null ) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.first(new XsdChoice((XmlElem) el))
            : ImList.empty();
    }

    public final XmlElem elem;

    public XsdChoice(XmlElem elem) {
        if( elem==null ) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
    }
}
