package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.Either;
import xyz.cofe.im.struct.ImList;
import xyz.cofe.im.struct.Result;
import xyz.cofe.xsd.om.xml.XmlAttr;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;

/*
https://www.w3schools.com/xml/el_any.asp

<any
id=ID
maxOccurs=nonNegativeInteger|unbounded
minOccurs=nonNegativeInteger
namespace=namespace
processContents=lax|skip|strict
any attributes
>

(annotation?)
 */
public final class XsdAny implements Xsd, IDAttribute, MaxOccursAttribute, MinOccursAttribute, XsdAnnotation.AnnotationProperty, ElementsLayout {
    public static final String Name = "any";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public static ImList<XsdAny> parseList(XmlNode el ){
        if( el==null ) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.first(new XsdAny((XmlElem) el))
            : ImList.empty();
    }

    public final XmlElem elem;

    @Override
    public XmlElem elem() {
        return elem;
    }

    public Result<String, String> getNamespace() {
        return Result.of(
            elem.attrib("namespace").map(XmlAttr::getValue).head(),
            "namespace not found"
        );
    }

    public Result<String, String> getProcessContents() {
        return Result.of(
            elem.attrib("processContents").map(XmlAttr::getValue).head(),
            "processContents not found"
        );
    }

    public XsdAny(XmlElem elem) {
        if( elem==null ) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
    }
}
