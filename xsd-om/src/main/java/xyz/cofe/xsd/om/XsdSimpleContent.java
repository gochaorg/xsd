package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;

/**
 The <a href="https://www.w3schools.com/xml/el_simpleContent.asp">simpleContent</a> element contains extensions or 
 restrictions on a text-only complex type or on a simple type as content and contains no elements.

<pre>
&lt;simpleContent
 id=ID ?
   Optional. Specifies a unique ID for the element

 any attributes ?
   Optional. Specifies any other attributes with non-schema namespace
&gt;

({@link XsdAnnotation annotation}?,({@link XsdRestriction restriction}|{@link XsdExtension extension}))

&lt;/simpleContent&gt;
 </pre>
 
 */
public final class XsdSimpleContent implements Xsd, ContentDef, IDAttribute {
    public static final String Name = "simpleContent";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public static ImList<XsdSimpleContent> parseList(XmlNode el) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.first(new XsdSimpleContent((XmlElem) el))
            : ImList.empty();
    }

    public final XmlElem elem;

    @Override
    public XmlElem elem() {
        return elem;
    }

    public XsdSimpleContent(XmlElem elem) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
    }
}
