package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.im.struct.Result;
import xyz.cofe.xsd.om.xml.XmlAttr;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Arrays;
import java.util.Objects;

/**
The <a href="https://www.w3schools.com/xml/el_union.asp">union</a> element defines a simple 
 type as a collection (union) of values from specified simple data types.

<pre>
&lt;union
 id=ID ?
   Optional. Specifies a unique ID for the element

 memberTypes="list of QNames" ?
   Optional.
   Specifies a list of built-in data types or
   simpleType elements defined in a schema

 any attributes ?
   Optional. Specifies any other
   attributes with non-schema namespace
&gt;

({@link XsdAnnotation annotation}?,({@link XsdSimpleType simpleType}*))

&lt;/union&gt;
</pre>
 
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
