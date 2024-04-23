package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.im.struct.Result;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;

/**
 The <a href="https://www.w3schools.com/xml/el_key.asp">key</a> element specifies an attribute or element value as a key (unique, non-nullable, and always present) within the containing element in an instance document.

 <p></p>
 The key element MUST contain the following (in order):

 <ul>
    <li>
        one and only one selector element
        (contains an XPath expression that specifies the set of elements across which the values specified by field must be unique)
    </li>

    <li>
        one or more field elements (contains an XPath expression that
        specifies the values that must be unique for the set of elements specified by the selector element)
    </li>
 </ul>

<pre>
&lt;key
 id=ID ?
   Optional. Specifies a unique ID for the element

 name=NCName
   Required. Specifies the name of the key element

 any attributes ?
   Optional. Specifies any other attributes with non-schema namespace
&gt;

({@link XsdAnnotation annotation}?,({@link XsdSelector selector},{@link XsdField field}+))

&lt;/key&gt;
 </pre>
 */
public final class XsdKey implements Xsd,
                                     IDAttribute,
                                     NameAttribute,
                                     XsdAnnotation.AnnotationProperty {
    public static final String Name = "key";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public static ImList<XsdKey> parseList(XmlNode el) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.first(new XsdKey((XmlElem) el))
            : ImList.empty();
    }

    public final XmlElem elem;

    @Override
    public XmlElem elem() {
        return elem;
    }

    public XsdKey(XmlElem elem) {
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
