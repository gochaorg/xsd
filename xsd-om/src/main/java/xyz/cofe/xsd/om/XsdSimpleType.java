package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.im.struct.Result;
import xyz.cofe.xsd.om.xml.XmlAttr;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;
import java.util.Optional;

/*
https://www.w3schools.com/xml/el_simpletype.asp

<simpleType
id=ID
name=NCName
any attributes
>

(annotation?,(restriction|list|union))

</simpleType>
 */
public final class XsdSimpleType implements Xsd, TypeDef {
    public static final String Name = "simpleType";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public static ImList<XsdSimpleType> parseList(XmlNode el) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.first(new XsdSimpleType((XmlElem) el))
            : ImList.empty();
    }

    public final XmlElem elem;

    public XsdSimpleType(XmlElem elem) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
    }

    public Result<BuiltInTypes.ID,String> getId(){
        return Result.of(elem.attrib("id").head(), "id not found")
            .map(XmlAttr::getValue)
            .flatMap(BuiltInTypes.ID::parse);
    }

    public ImList<XsdAnnotation> getAnnotations() {
        return elem.getChildren().flatMap(XsdAnnotation::parseList);
    }

    public Optional<SimpleTypeContent> getRestriction() {
        Optional<SimpleTypeContent> c1 = elem.getChildren().flatMap(XsdRestriction::parseList).head().map( a->a );
        Optional<SimpleTypeContent> c2 = elem.getChildren().flatMap(XsdList::parseList).head().map( a->a );
        Optional<SimpleTypeContent> c3 = elem.getChildren().flatMap(XsdUnion::parseList).head().map( a->a );
        return c1.or(()->c2).or(()->c3);
    }
}
