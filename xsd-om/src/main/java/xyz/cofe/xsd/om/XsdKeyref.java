package xyz.cofe.xsd.om;

import xyz.cofe.coll.im.ImList;
import xyz.cofe.coll.im.Result;
import xyz.cofe.xml.XmlElem;
import xyz.cofe.xml.XmlNode;

import java.util.Objects;
import java.util.Optional;

/**
 The <a href="https://www.w3schools.com/xml/el_keyref.asp">keyref</a> element specifies
 that an attribute or element value correspond to those of the specified key or unique element.

 <p></p>
 The keyref element MUST contain the following (in order):

 <ul>
  <li>
    one and only one selector element  (contains an XPath expression that specifies the set of elements across which the values specified by field must be unique)
  </li>
  <li>
    one or more field elements (contains an XPath expression that specifies the values that must be unique for the set of elements specified by the selector element)
  </li>
 </ul>

<pre> 
&lt;keyref
 id=ID ?
   Optional. Specifies a unique ID for the element

 name=NCName
   Required. Specifies the name of the keyref element

 refer=QName
   Required. Specifies the name of a key or unique element defined in this or another schema

 any attributes ?
   Optional. Specifies any other attributes with non-schema namespace
&gt;

({@link XsdAnnotation annotation}?,({@link XsdSelector selector},{@link XsdField field}+))

&lt;/keyref&gt;
 </pre>
 */
public final class XsdKeyref implements Xsd, XsdAnnotation.AnnotationProperty, IDAttribute, NameAttribute {
    public static final String Name = "keyref";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public static ImList<XsdKeyref> parseList(XmlNode el, Xsd parent) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.of(new XsdKeyref((XmlElem) el, parent))
            : ImList.of();
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

    public XsdKeyref(XmlElem elem, Xsd parent) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
        this.parent = Optional.ofNullable(parent);
    }

    public Result<XsdSelector, String> getSelectors() {
        return Result.from(
            elem().getChildren().fmap(n -> XsdSelector.parseList(n,this)).head(),
            ()->"nested selector not found"
        );
    }

    public ImList<XsdField> getFields() {
        return elem().getChildren().fmap(n -> XsdField.parseList(n,this));
    }
}
