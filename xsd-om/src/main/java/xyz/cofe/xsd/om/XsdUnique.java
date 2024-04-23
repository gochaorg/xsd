package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.im.struct.Result;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;

/**
 The <a href="https://www.w3schools.com/xml/el_unique.asp">unique</a> element defines
 that an element or an attribute value must be unique within the scope.

 <p></p>
 The unique element MUST contain the following (in order):

 <ul>
     <li>
        one and only one selector element  (contains an
        XPath expression that specifies the set of
        elements across which the values specified by field must be unique)
     </li>
     <li>
        one or more field elements (contains an XPath expression
        that specifies the values that must be unique for
        the set of elements specified by the selector element)
     </li>
 </ul>

<pre>
&lt;unique
 id=ID ?
   Optional. Specifies a unique ID for the element

 name=NCName
   Required. Specifies a name for the element

 any attributes ?
   Optional. Specifies any other attributes with non-schema namespace
&gt;

({@link XsdAnnotation annotation}?,({@link XsdSelector selector},{@link XsdField field}+))

&lt;/unique&gt;
</pre>
 */
public final class XsdUnique implements Xsd, XsdAnnotation.AnnotationProperty, IDAttribute, NamespaceAttribute {
    public static final String Name = "unique";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public static ImList<XsdUnique> parseList(XmlNode el) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.first(new XsdUnique((XmlElem) el))
            : ImList.empty();
    }

    public final XmlElem elem;

    @Override
    public XmlElem elem() {
        return elem;
    }

    public XsdUnique(XmlElem elem) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
    }

    public Result<XsdSelector,String> getSelector(){
        return Result.of(
            elem().getChildren().flatMap(XsdSelector::parseList).head(),
            "selector not found"
        );
    }

    public ImList<XsdField> getFields(){ return elem().getChildren().flatMap(XsdField::parseList); }
}
