package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.im.struct.Result;
import xyz.cofe.xml.XmlAttr;
import xyz.cofe.xml.XmlElem;
import xyz.cofe.xml.XmlNode;

import java.util.Objects;
import java.util.Optional;

/**
 The <a href="https://www.w3schools.com/xml/el_anyattribute.asp">anyAttribute</a> element enables the author to extend
 the XML document with attributes not specified by the schema.

 <pre>
&lt;anyAttribute
 id=ID ?
 namespace=namespace ?

   Optional.
   Specifies the namespaces containing the attributes that can be used.
   Can be set to one of the following:

   ##any - attributes from any namespace is allowed (this is default)
   ##other - attributes from any namespace that is not the namespace of the parent element can be present
   ##local - attributes must come from no namespace
   ##targetNamespace - attributes from the namespace of the parent element can be present
   List of {URI references of namespaces, ##targetNamespace, ##local} - attributes
     from a space-delimited list of the namespaces can be present

 processContents=lax|skip|strict ?

   Optional. Specifies how the XML processor should handle
   validation against the elements specified by this any element.
   Can be set to one of the following:

     strict - the XML processor must obtain
              the schema for the required namespaces and validate the elements (this is default)
     lax - same as strict but;
           if the schema cannot be obtained, no errors will occur
     skip - The XML processor does not attempt
            to validate any elements from the specified namespaces

 any attributes ?
&gt;

({@link XsdAnnotation annotation}?)

&lt;/anyAttribute&gt;
 </pre>
 */
public final class XsdAnyAttribute implements Xsd,
                                              IDAttribute,
                                              NamespaceAttribute,
                                              XsdAnnotation.AnnotationProperty {
    public static final String Name = "anyAttribute";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public static ImList<XsdAnyAttribute> parseList(XmlNode el, Xsd parent) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.first(new XsdAnyAttribute((XmlElem) el, parent))
            : ImList.empty();
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

    public XsdAnyAttribute(XmlElem elem, Xsd parent) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
        this.parent = Optional.ofNullable(parent);
    }

    public Result<String, String> getProcessContents() {
        return Result.of(
            elem.attrib("processContents").map(XmlAttr::getValue).head(),
            "processContents not found"
        );
    }
}
