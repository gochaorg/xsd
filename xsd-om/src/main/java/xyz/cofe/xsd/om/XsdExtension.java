package xyz.cofe.xsd.om;

import xyz.cofe.coll.im.ImList;
import xyz.cofe.coll.im.Result;
import xyz.cofe.xml.XmlElem;
import xyz.cofe.xml.XmlNode;

import java.util.Objects;
import java.util.Optional;

/**

 The <a href="https://www.w3schools.com/xml/el_extension.asp">extension</a> element extends an existing simpleType or complexType element.

<pre>
&lt;extension
 id=ID ?
   Optional. Specifies a unique ID for the element

 base=QName
   Required. Specifies the name of a built-in data type, a simpleType element, or a complexType element

 any attributes ?
 Optional. Specifies any other attributes with non-schema namespace
&gt;

(
 {@link XsdAnnotation annotation}?,

  ( ({@link XsdGroup group}|{@link XsdAll all}|{@link XsdChoice choice}|{@link XsdSequence sequence})? ,

    ( ({@link XsdAttribute attribute} | {@link XsdAttributeGroup attributeGroup})*,
      {@link XsdAnyAttribute anyAttribute}?
    )
  )
)
 
&lt;/extension&gt;
 </pre>
 */
@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "OptionalAssignedToNull"})
public final class XsdExtension implements Xsd,
                                           XsdComplexContent.Nested,
                                           IDAttribute,
                                           BaseAttribute,
                                           XsdAnnotation.AnnotationProperty,
                                           XsdSimpleContent.Nested {
    public static final String Name = "extension";

    public static boolean isMatch(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Name);
    }

    public static ImList<XsdExtension> parseList(XmlNode el, Xsd parent) {
        if (el == null) throw new IllegalArgumentException("el==null");
        return isMatch(el)
            ? ImList.of(new XsdExtension((XmlElem) el, parent))
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

    public XsdExtension(XmlElem elem, Xsd parent) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        this.elem = elem;
        this.parent = Optional.ofNullable(parent);
    }

    public sealed interface NestedEl permits XsdGroup, XsdAll, XsdChoice, XsdSequence {
        public static ImList<NestedEl> parseList(XmlNode node, Xsd parent){
            ImList<NestedEl> r1 = XsdGroup.parseList(node, parent).map(a->a);
            ImList<NestedEl> r2 = XsdAll.parseList(node, parent).map(a->a);
            ImList<NestedEl> r3 = XsdChoice.parseList(node, parent).map(a->a);
            ImList<NestedEl> r4 = XsdSequence.parseList(node, parent).map(a->a);
            return r1.append(r2).append(r3).append(r4);
        }
    }

    public Optional<TypeDef> getParentTypeDef(){
        Optional<Xsd> prnt = getParent();
        while (prnt.isPresent()){
            var p = prnt.get();
            if( p instanceof TypeDef t)return Optional.of(t);

            prnt = p.getParent();
        }
        return Optional.empty();
    }

    private Optional<NestedEl> nested;
    public Optional<NestedEl> getNested(){
        if( nested!=null )return nested;
        nested = elem().getChildren().fmap(n -> NestedEl.parseList(n,this)).head();
        return nested;
    }

    private Optional<XsdAnyAttribute> anyAttribute;
    public Optional<XsdAnyAttribute> getAnyAttribute(){
        if( anyAttribute!=null )return anyAttribute;
        anyAttribute = elem().getChildren().fmap(n -> XsdAnyAttribute.parseList(n,this)).head();
        return anyAttribute;
    }

    private ImList<XsdAttribute> attributes;
    public ImList<XsdAttribute> getAttributes(){
        if( attributes!=null )return attributes;
        attributes = elem().getChildren().fmap(n -> XsdAttribute.parseList(n,this));
        return attributes;
    }

    private ImList<XsdAttributeGroup> attributeGroups;
    public ImList<XsdAttributeGroup> getAttributeGroups(){
        if( attributeGroups!=null )return attributeGroups;
        attributeGroups = elem().getChildren().fmap(n -> XsdAttributeGroup.parseList(n,this));
        return attributeGroups;
    }

    private Result<ImList<TypeDef>, String> refTypes;
    public Result<ImList<TypeDef>, String> getRefTypes() {
        if( refTypes!=null )return refTypes;
        refTypes = getBase().fmap( typeQName -> TypeDef.resolveTypeDefs(typeQName, this) );
        return refTypes;
    }

    public Result<TypeDef,String> getRefType(){
        return getRefTypes().fmap( types -> types.size()==1 ?
            Result.from(types.head(), ()->"expect one ref types") : Result.error("expect one ref types")
        );
    }

}
