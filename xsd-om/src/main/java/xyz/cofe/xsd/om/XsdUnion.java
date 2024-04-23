package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.im.struct.Result;
import xyz.cofe.xsd.om.xml.XmlAttr;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Arrays;
import java.util.Objects;

/*
https://www.w3schools.com/xml/el_union.asp

<union
id=ID
memberTypes="list of QNames"
any attributes
>

(annotation?,(simpleType*))

</union>
 */
public final class XsdUnion implements Xsd,
                                       SimpleTypeContent,
                                       IDAttribute,
                                       XsdAnnotation.AnnotationProperty {
    public static final String Name = "union";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public static ImList<XsdUnion> parseList(XmlNode el) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.first(new XsdUnion((XmlElem) el))
            : ImList.empty();
    }

    public final XmlElem elem;

    @Override
    public XmlElem elem() {
        return elem;
    }

    public XsdUnion(XmlElem elem) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
    }

    public Result<ImList<BuiltInTypes.QName>, String> getMemberTypes() {
        return Result.of(
            elem().attrib("memberTypes").map(XmlAttr::getValue).head(),
            "memberTypes not found"
        ).map( str ->
            ImList.from(Arrays.asList(str.split("\\s+")))
        ).map( strings ->
            strings.flatMap( string -> BuiltInTypes.QName.parse(string).toImList())
        );
    }

    public ImList<XsdSimpleType> getSimpleTypes(){ return elem().getChildren().flatMap(XsdSimpleType::parseList); }
}
