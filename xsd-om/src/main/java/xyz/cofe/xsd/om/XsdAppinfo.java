package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.im.struct.Result;
import xyz.cofe.xml.XmlAttr;
import xyz.cofe.xml.XmlElem;
import xyz.cofe.xml.XmlNode;

import java.util.Objects;
import java.util.Optional;

/*
https://www.w3schools.com/xml/el_appinfo.asp

<appinfo
source=anyURL
>

Any well-formed XML content

</appinfo>
 */
public final class XsdAppinfo implements Xsd {
    public static final String Name = "appinfo";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public static ImList<XsdAppinfo> parseList(XmlNode el, Xsd parent) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.first(new XsdAppinfo((XmlElem) el, parent))
            : ImList.empty();
    }

    public final XmlElem elem;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public final Optional<Xsd> parent;

    @Override
    public Optional<Xsd> getParent() {
        return parent;
    }

    @Override
    public XmlElem elem() {
        return elem;
    }

    public XsdAppinfo(XmlElem elem, Xsd parent) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
        this.parent = Optional.ofNullable(parent);
    }

    public Result<BuiltInTypes.AnyURI, String> getSource() {
        return Result.of(
            elem.attrib("source").map(XmlAttr::getValue).head(),
            "source not found"
        ).flatMap(BuiltInTypes.AnyURI::parse);
    }
}
