package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.im.struct.Result;
import xyz.cofe.xml.XmlAttr;
import xyz.cofe.xml.XmlElem;
import xyz.cofe.xml.XmlNode;

import java.util.Objects;
import java.util.Optional;

/**
 The <a href="https://www.w3schools.com/xml/el_any.asp">any</a> element enables the author to extend the XML document with elements not specified by the schema.
 
 <pre>
&lt;any
  id=ID ?
  maxOccurs=nonNegativeInteger|unbounded ?
  minOccurs=nonNegativeInteger ?
  namespace=namespace ?

     Specifies the namespaces containing the elements that can be used.
     Can be set to one of the following:

       ##any   - elements from any namespace is allowed (this is default)
       ##other - elements from any namespace that is not
                 the namespace of the parent element can be present
       ##local - elements must come from no namespace
       ##targetNamespace - elements from the namespace of the parent element can be present
       List of {URI references of namespaces, ##targetNamespace, ##local}
          - elements from a space-delimited list of the namespaces can be present

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

 &lt;/any&gt; 
</pre>
 */
public final class XsdAny implements Xsd,
                                     IDAttribute,
                                     MaxOccursAttribute,
                                     MinOccursAttribute,
                                     XsdAnnotation.AnnotationProperty,
                                     ElementsLayout {
    public static final String Name = "any";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public static ImList<XsdAny> parseList(XmlNode el, Xsd parent) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.first(new XsdAny((XmlElem) el, parent))
            : ImList.empty();
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

    public Result<String, String> getNamespace() {
        return Result.of(
            elem.attrib("namespace").map(XmlAttr::getValue).head(),
            "namespace not found"
        );
    }

    public Result<String, String> getProcessContents() {
        return Result.of(
            elem.attrib("processContents").map(XmlAttr::getValue).head(),
            "processContents not found"
        );
    }

    public XsdAny(XmlElem elem, Xsd parent) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
        this.parent = Optional.ofNullable(parent);
    }
}
