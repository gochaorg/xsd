package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.im.struct.Result;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;

/*
https://www.w3schools.com/xml/el_keyref.asp

<keyref
id=ID
name=NCName
refer=QName
any attributes
>

(annotation?,(selector,field+))

</keyref>
 */
public final class XsdKeyref implements Xsd, XsdAnnotation.AnnotationProperty, IDAttribute, NameAttribute {
    public static final String Name = "keyref";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public static ImList<XsdKeyref> parseList(XmlNode el) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.first(new XsdKeyref((XmlElem) el))
            : ImList.empty();
    }

    public final XmlElem elem;

    @Override
    public XmlElem elem() {
        return elem;
    }

    public XsdKeyref(XmlElem elem) {
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
