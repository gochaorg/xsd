package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.im.struct.Result;
import xyz.cofe.xsd.om.xml.XmlAttr;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;

/*
https://www.w3schools.com/xml/el_anyattribute.asp

<anyAttribute
id=ID
namespace=namespace
processContents=lax|skip|strict
any attributes
>

(annotation?)

</anyAttribute>
 */
public final class XsdAnyAttribute implements Xsd, IDAttribute, NamespaceAttribute {
    public static final String Name = "anyAttribute";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public static ImList<XsdAnyAttribute> parseList(XmlNode el ){
        if( el==null ) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.first(new XsdAnyAttribute((XmlElem) el))
            : ImList.empty();
    }

    public final XmlElem elem;

    @Override
    public XmlElem elem() {
        return elem;
    }

    public XsdAnyAttribute(XmlElem elem) {
        if( elem==null ) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
    }

    public Result<String, String> getProcessContents() {
        return Result.of(
            elem.attrib("processContents").map(XmlAttr::getValue).head(),
            "processContents not found"
        );
    }
}
