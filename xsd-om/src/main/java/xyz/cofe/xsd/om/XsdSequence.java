package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;

/*
https://www.w3schools.com/xml/el_sequence.asp

<sequence
id=ID
maxOccurs=nonNegativeInteger|unbounded
minOccurs=nonNegativeInteger
any attributes
>

(annotation?,(element|group|choice|sequence|any)*)

</sequence>
 */
public final class XsdSequence implements Xsd, ElementsLayout {
    public static final String Sequence = "sequence";

    public static boolean isSequence(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
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
