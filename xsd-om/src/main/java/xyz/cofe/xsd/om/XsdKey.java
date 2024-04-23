package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.im.struct.Result;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;

/*
https://www.w3schools.com/xml/el_key.asp

<key
id=ID
name=NCName
any attributes
>

(annotation?,(selector,field+))

</key>
 */
public final class XsdKey implements Xsd,
                                     IDAttribute,
                                     NameAttribute,
                                     XsdAnnotation.AnnotationProperty {
    public static final String Name = "key";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public static ImList<XsdKey> parseList(XmlNode el) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.first(new XsdKey((XmlElem) el))
            : ImList.empty();
    }

    public final XmlElem elem;

    @Override
    public XmlElem elem() {
        return elem;
    }

    public XsdKey(XmlElem elem) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
    }

    public Result<XsdSelector, String> getSelectors() {
        return Result.of(
            elem().getChildren().flatMap(XsdSelector::parseList).head(),
            "nested selector not found"
        );
    }

    public ImList<XsdField> getFields() {
        return elem().getChildren().flatMap(XsdField::parseList);
    }
}
