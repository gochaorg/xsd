package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.im.struct.Result;
import xyz.cofe.xsd.om.xml.XmlAttr;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;

/**
 The <a href="https://www.w3schools.com/xml/el_annotation.asp">annotation</a> element is a top level element that specifies schema comments. The comments serve as inline documentation.

 <pre>
&lt;annotation
  id=ID?
  any attributes?
&gt;

({@link XsdAppinfo appinfo}|{@link XsdDocumentation documentation})*

&lt;/annotation&gt;
 </pre>
 */
public final class XsdAnnotation implements Xsd, IDAttribute {
    public non-sealed interface AnnotationProperty extends Xsd {
        public default ImList<XsdAnnotation> getAnnotations() {
            return elem().getChildren().flatMap(XsdAnnotation::parseList);
        }
    }

    public static ImList<XsdAnnotation> parseList( XmlNode el ){
        if( el==null ) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.first(new XsdAnnotation((XmlElem) el))
            : ImList.empty();
    }

    public static final String Name = "annotation";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public final XmlElem elem;

    @Override
    public XmlElem elem() {
        return elem;
    }

    public XsdAnnotation(XmlElem elem) {
        if( elem==null ) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
    }

    public ImList<XsdDocumentation> getDocumentations(){
        return elem.getChildren().flatMap(XsdDocumentation::parseList);
    }
}
