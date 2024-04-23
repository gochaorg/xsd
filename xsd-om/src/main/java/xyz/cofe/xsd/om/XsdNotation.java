package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.im.struct.Result;
import xyz.cofe.xsd.om.xml.XmlAttr;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;

/*
https://www.w3schools.com/xml/el_notation.asp

<notation
id=ID
name=NCName
public=anyURI
system=anyURI
any attributes
>

(annotation?)

</notation>
 */
public final class XsdNotation implements Xsd, IDAttribute, NamespaceAttribute, XsdAnnotation.AnnotationProperty {
    public static final String Name = "notation";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public static ImList<XsdNotation> parseList(XmlNode el) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.first(new XsdNotation((XmlElem) el))
            : ImList.empty();
    }

    public final XmlElem elem;

    @Override
    public XmlElem elem() {
        return elem;
    }

    public XsdNotation(XmlElem elem) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
    }

    public Result<BuiltInTypes.AnyURI, String> getPublic() {
        return Result.of(
            elem().attrib("public").map(XmlAttr::getValue).head(),
            "public not found"
        ).flatMap(BuiltInTypes.AnyURI::parse);
    }

    public Result<BuiltInTypes.AnyURI, String> getSystem() {
        return Result.of(
            elem().attrib("system").map(XmlAttr::getValue).head(),
            "system not found"
        ).flatMap(BuiltInTypes.AnyURI::parse);
    }
}
