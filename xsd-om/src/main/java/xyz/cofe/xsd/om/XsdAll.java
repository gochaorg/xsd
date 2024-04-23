package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.im.struct.Result;
import xyz.cofe.xsd.om.xml.XmlAttr;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;

/**
The <a href="https://www.w3schools.com/xml/el_all.asp">all</a> element specifies that the child elements
 can appear in <b>any order</b> and that each child element can occur zero or one time.

 <pre>
&lt;all
  id=ID?
  maxOccurs=1?    <i>The value must be 1.</i>
  minOccurs=0|1?  <i>The value can be 0 or 1. Default value is 1</i>
  any attributes?
&gt;

({@link XsdAnnotation annotation}?,{@link XsdElement element}*)

&lt;/all&gt;
 </pre>
*/
public final class XsdAll implements Xsd,
                                     ElementsLayout,
                                     IDAttribute,
                                     XsdGroup.Nested,
                                     XsdExtension.NestedEl {
    public static final String Name = "all";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public static ImList<XsdAll> parseList(XmlNode el ){
        if( el==null ) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.first(new XsdAll((XmlElem) el))
            : ImList.empty();
    }

    public final XmlElem elem;

    @Override
    public XmlElem elem() {
        return elem;
    }

    public XsdAll(XmlElem elem) {
        if( elem==null ) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
    }

    public ImList<XsdAppinfo> getAppinfos(){ return elem.getChildren().flatMap(XsdAppinfo::parseList); }
    public ImList<XsdDocumentation> getDocumentations(){ return elem.getChildren().flatMap(XsdDocumentation::parseList); }
}
