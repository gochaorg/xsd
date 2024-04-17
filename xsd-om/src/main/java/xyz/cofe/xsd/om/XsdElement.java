package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;

public final class XsdElement implements Xsd {
    public static final String Element = "element";

    public static boolean isElement(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), XsdConst.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Element);
    }

    public static ImList<XsdElement> parseList(XmlNode el ){
        if( el==null ) throw new IllegalArgumentException("el==null");
        return isElement(el)
            ? ImList.first(new XsdElement((XmlElem) el))
            : ImList.empty();
    }

    public final XmlElem elem;

    public XsdElement(XmlElem elem) {
        if( elem==null ) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
    }

    public ImList<XsdAnnotation> getAnnotations(){
        return elem.getChildren().flatMap(XsdAnnotation::parseList);
    }

    public ImList<XsdUnique> getUniques(){
        return elem.getChildren().flatMap(XsdUnique::parseList);
    }

    public ImList<XsdKey> getKeys(){
        return elem.getChildren().flatMap(XsdKey::parseList);
    }

    public ImList<XsdKeyref> getKeyrefs(){
        return elem.getChildren().flatMap(XsdKeyref::parseList);
    }
}
