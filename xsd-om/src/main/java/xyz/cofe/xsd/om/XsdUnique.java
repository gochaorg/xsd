package xyz.cofe.xsd.om;

import xyz.cofe.coll.im.ImList;
import xyz.cofe.coll.im.Result;
import xyz.cofe.xml.XmlElem;
import xyz.cofe.xml.XmlNode;

import java.util.Objects;
import java.util.Optional;

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
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class XsdUnique implements Xsd, XsdAnnotation.AnnotationProperty, IDAttribute, NamespaceAttribute {
    public static final String Name = "unique";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public static ImList<XsdUnique> parseList(XmlNode el, Xsd parent) {
        if (el == null) throw new IllegalArgumentException("el==null");
        if( parent==null ) throw new IllegalArgumentException("parent==null");
        return isMatch(el)
            ? ImList.of(new XsdUnique((XmlElem) el, parent))
            : ImList.of();
    }

    public final XmlElem elem;
    public final Optional<Xsd> parent;

    @Override
    public Optional<Xsd> getParent() {
        return parent;
    }

    @Override
    public XmlElem elem() {
        return elem;
    }

    public XsdUnique(XmlElem elem, Xsd parent) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
        this.parent = Optional.ofNullable(parent);
    }

    public Result<XsdSelector,String> getSelector(){
        return Result.from(
            elem().getChildren().fmap(n -> XsdSelector.parseList(n,this)).head(),
            ()->"selector not found"
        );
    }

    public ImList<XsdField> getFields(){ return elem().getChildren().fmap(n -> XsdField.parseList(n,this)); }
}
