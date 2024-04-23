package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.im.struct.Result;
import xyz.cofe.xsd.om.xml.XmlAttr;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;

/*
https://www.w3schools.com/xml/el_attribute.asp

<attribute
default=string
fixed=string
form=qualified|unqualified
id=ID
name=NCName
ref=QName
type=QName
use=optional|prohibited|required
any attributes
>

(annotation?,(simpleType?))

</attribute>
 */
public final class XsdAttribute implements Xsd, IDAttribute, TypeAttribute, RefAttribute, NameAttribute {
    public static final String Name = "attribute";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public static ImList<XsdAttribute> parseList(XmlNode el ){
        if( el==null ) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.first(new XsdAttribute((XmlElem) el))
            : ImList.empty();
    }

    public final XmlElem elem;

    @Override
    public XmlElem elem() {
        return elem;
    }

    public XsdAttribute(XmlElem elem) {
        if( elem==null ) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
    }

    public Result<String, String> getDefault() {
        return Result.of(
            elem.attrib("default").map(XmlAttr::getValue).head(),
            "default not found"
        );
    }

    public Result<String, String> getFixed() {
        return Result.of(
            elem.attrib("fixed").map(XmlAttr::getValue).head(),
            "fixed not found"
        );
    }

    public Result<String, String> getForm() {
        return Result.of(
            elem.attrib("form").map(XmlAttr::getValue).head(),
            "form not found"
        );
    }

    public Result<String, String> getUse() {
        return Result.of(
            elem.attrib("use").map(XmlAttr::getValue).head(),
            "use not found"
        );
    }
}
