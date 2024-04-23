package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.im.struct.Result;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;

/*
https://www.w3schools.com/xml/el_unique.asp

<unique
id=ID
name=NCName
any attributes
>

(annotation?,(selector,field+))

</unique>
 */
public final class XsdUnique implements Xsd, XsdAnnotation.AnnotationProperty, IDAttribute, NamespaceAttribute {
    public static final String Name = "unique";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public static ImList<XsdUnique> parseList(XmlNode el) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.first(new XsdUnique((XmlElem) el))
            : ImList.empty();
    }

    public final XmlElem elem;

    @Override
    public XmlElem elem() {
        return elem;
    }

    public XsdUnique(XmlElem elem) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
    }

    public Result<XsdSelector,String> getSelector(){
        return Result.of(
            elem().getChildren().flatMap(XsdSelector::parseList).head(),
            "selector not found"
        );
    }

    public ImList<XsdField> getFields(){ return elem().getChildren().flatMap(XsdField::parseList); }
}
