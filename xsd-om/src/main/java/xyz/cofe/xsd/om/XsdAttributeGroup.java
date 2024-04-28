package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.xml.XmlElem;
import xyz.cofe.xml.XmlNode;

import java.util.Objects;
import java.util.Optional;

/**
 The <a href="https://www.w3schools.com/xml/el_attributegroup.asp">attributeGroup</a> element is used to group
 a set of attribute declarations so that they can be incorporated as a group into complex type definitions.

 <pre>
&lt;attributeGroup
 id=ID ?
   Optional. Specifies a unique ID for the element

 name=NCName ?
   Optional.
   Specifies the name of the attribute group.
   Name and ref attributes cannot both be present

 ref=QName ?
   Optional.
   Specifies a reference to a named attribute group.
   Name and ref attributes cannot both be present

 any attributes ?
   Optional.
   Specifies any other attributes with non-schema namespace
&gt;

({@link XsdAnnotation annotation}?),(({@link XsdAttribute attribute}|{@link XsdAttributeGroup attributeGroup})*,{@link XsdAnyAttribute anyAttribute}?))

&lt;/attributeGroup&gt;
 </pre>
 */
@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "OptionalAssignedToNull"})
public final class XsdAttributeGroup implements Xsd,
                                                IDAttribute,
                                                NameAttribute,
                                                RefAttribute,
                                                XsdAnnotation.AnnotationProperty {
    public static final String Name = "attributeGroup";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public static ImList<XsdAttributeGroup> parseList(XmlNode el, Xsd parent) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.first(new XsdAttributeGroup((XmlElem) el, parent))
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

    public XsdAttributeGroup(XmlElem elem, Xsd parent) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
        this.parent = Optional.ofNullable(parent);
    }

    private ImList<XsdAttribute> attributes;
    public ImList<XsdAttribute> getAttributes(){
        if( attributes!=null )return attributes;
        attributes = elem().getChildren().flatMap(n -> XsdAttribute.parseList(n,this));
        return attributes;
    }

    private ImList<XsdAttributeGroup> attributeGroups;
    public ImList<XsdAttributeGroup> getAttributeGroups(){
        if( attributeGroups!=null )return attributeGroups;
        attributeGroups = elem().getChildren().flatMap(n -> XsdAttributeGroup.parseList(n,this));
        return attributeGroups;
    }

    private Optional<XsdAnyAttribute> anyAttribute;
    public Optional<XsdAnyAttribute> getAnyAttribute(){
        if( anyAttribute!=null )return anyAttribute;
        anyAttribute = elem().getChildren().flatMap(n -> XsdAnyAttribute.parseList(n,this)).head();
        return anyAttribute;
    }
}
