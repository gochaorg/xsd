package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.im.struct.Result;
import xyz.cofe.xsd.om.xml.XmlAttr;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;

/*
https://www.w3schools.com/xml/el_field.asp

<field
id=ID
xpath=XPath expression
any attributes
>

(annotation?)

</field>
 */
public final class XsdField implements Xsd, XsdAnnotation.AnnotationProperty, IDAttribute {
    public static final String Name = "field";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public static ImList<XsdField> parseList(XmlNode el ){
        if( el==null ) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.first(new XsdField((XmlElem) el))
            : ImList.empty();
    }

    public final XmlElem elem;

    @Override
    public XmlElem elem() {
        return elem;
    }

    public XsdField(XmlElem elem) {
        if( elem==null ) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
    }

    public Result<BuiltInTypes.ID, String> getId() {
        return Result.of(
            elem.attrib("id").map(XmlAttr::getValue).head(),
            "id not found"
        ).flatMap(BuiltInTypes.ID::parse);
    }

    public Result<String, String> getXPath() {
        return Result.of(
            elem.attrib("xpath").map(XmlAttr::getValue).head(),
            "xpath not found"
        );
    }
}
