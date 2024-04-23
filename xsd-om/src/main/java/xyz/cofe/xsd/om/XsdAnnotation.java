package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;

/*
https://www.w3schools.com/xml/el_annotation.asp

<annotation
id=ID
any attributes
>

(appinfo|documentation)*

</annotation>
 */
public final class XsdAnnotation implements Xsd {
    public static ImList<XsdAnnotation> parseList( XmlNode el ){
        if( el==null ) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.first(new XsdAnnotation((XmlElem) el))
            : ImList.empty();
    }

    public static final String Name = "annotation";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public final XmlElem elem;

    public XsdAnnotation(XmlElem elem) {
        if( elem==null ) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
    }

    public ImList<XsdDocumentation> getDocumentations(){
        return elem.getChildren().flatMap(XsdDocumentation::parseList);
    }
}
