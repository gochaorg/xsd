package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.im.struct.Result;
import xyz.cofe.im.struct.Tuple2;
import xyz.cofe.xml.XmlAttr;
import xyz.cofe.xml.XmlDoc;
import xyz.cofe.xml.XmlElem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/*
https://www.w3schools.com/xml/el_schema.asp

<schema
id=ID
attributeFormDefault=qualified|unqualified
elementFormDefault=qualified|unqualified
blockDefault=(#all|list of (extension|restriction|substitution))
finalDefault=(#all|list of (extension|restriction|list|union))
targetNamespace=anyURI
version=token
xmlns=anyURI
any attributes
>

((include|import|redefine|annotation)*,(((simpleType|complexType|
group|attributeGroup)|element|attribute|notation),annotation*)*)

</schema>
 */
@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "OptionalAssignedToNull"})
public final class XsdSchema implements Xsd, IDAttribute {
    public final XmlDoc xmlDoc;

    public XsdSchema(XmlDoc xmlDoc) {
        if (xmlDoc == null) throw new IllegalArgumentException("xmlDoc==null");
        this.xmlDoc = xmlDoc;
    }


    @Override
    public XmlElem elem() {
        return xmlDoc.getDocumentElement();
    }

    @Override
    public Optional<Xsd> getParent() {
        return Optional.empty();
    }

    //region includes : List<XsdInclude>
    private List<XsdInclude> includes;

    public List<XsdInclude> getIncludes() {
        if (includes != null) return includes;
        this.includes =
            xmlDoc.getDocumentElement().walk().elems()
                .flatMap(elem ->
                    XsdInclude.parse(elem, this)
                        .map(xsdInclude -> List.of(xsdInclude).iterator())
                        .orElse(Collections.emptyIterator()))
                .toList();
        return includes;
    }

    //endregion
    //region imports : List<XsdImport>
    private List<XsdImport> imports;

    public List<XsdImport> getImports() {
        if (imports != null) return imports;
        this.imports =
            xmlDoc.getDocumentElement().walk().elems()
                .flatMap(elem ->
                    XsdImport.parse(elem, this)
                        .map(xsdInclude -> List.of(xsdInclude).iterator())
                        .orElse(Collections.emptyIterator()))
                .toList();
        return imports;
    }

    //endregion
    //region nestedXsdDocs : List<XsdDoc>
    private List<XsdSchema> nestedXsdSchemas = null;

    public List<XsdSchema> getNestedXsdDocs() {
        if (nestedXsdSchemas != null) return nestedXsdSchemas;
        nestedXsdSchemas = getNestedXsdDocs(true);
        return nestedXsdSchemas;
    }

    public List<XsdSchema> getNestedXsdDocs(boolean includeSelf) {
        var visited = new HashSet<XsdSchema>();
        var nestedXsdDocs = new ArrayList<XsdSchema>();

        if (includeSelf) {
            visited.add(this);
            nestedXsdDocs.add(this);
        }

        Consumer<XsdSchema> accept = xsdDoc -> {
            if (visited.contains(xsdDoc)) return;
            visited.add(xsdDoc);
            nestedXsdDocs.add(xsdDoc);
        };

        getImports().forEach(imp -> imp.getXsdDocs().values().forEach(xsdRes ->
            {
                xsdRes.doc().each(accept);
                xsdRes.doc().map(x -> x.getNestedXsdDocs(false)).each(lst -> lst.forEach(accept));
            }
        ));

        getIncludes().forEach(imp -> imp.getXsdDocs().values().forEach(xsdRes ->
            {
                xsdRes.doc().each(accept);
                xsdRes.doc().map(x -> x.getNestedXsdDocs(false)).each(lst -> lst.forEach(accept));
            }
        ));

        return nestedXsdDocs;
    }

    //endregion
    //region nestedErrors : List<String>
    public List<String> getNestedErrors() {
        var lst = new ArrayList<String>();
        getImports().forEach(imp -> {
            imp.getXsdDocs().values().forEach(ld -> {
                ld.doc().toErrOptional().ifPresent(lst::add);
                ld.doc().toOptional().ifPresent(xd -> lst.addAll(xd.getNestedErrors()));
            });
        });
        getIncludes().forEach(imp -> {
            imp.getXsdDocs().values().forEach(ld -> {
                ld.doc().toErrOptional().ifPresent(lst::add);
                ld.doc().toOptional().ifPresent(xd -> lst.addAll(xd.getNestedErrors()));
            });
        });
        return lst;
    }
    //endregion

    //region targetNamespace : Optional<String>
    private Optional<String> targetNamespace;

    public Optional<String> getTargetNamespace() {
        if (targetNamespace != null) return targetNamespace;
        targetNamespace = xmlDoc.getDocumentElement().attrib("targetNamespace").head().map(XmlAttr::getValue);
        return targetNamespace;
    }
    //endregion
    //region namespacePrefixes : Map<String, Function<Namespaces, Result<Namespace, String>>>
    private Map<String, Function<Namespaces, Result<Namespace, String>>>  namespacePrefixes;
    public Map<String, Function<Namespaces, Result<Namespace, String>>> getNamespacePrefixes() {
        if( namespacePrefixes!=null )return namespacePrefixes;

        Map<String, Function<Namespaces, Result<Namespace, String>>> namespacePrefixes;
        namespacePrefixes = xmlDoc.getDocumentElement().getAttributes()
            .filter(a -> "xmlns".equals(a.getPrefix()))
            .toMap(
                a -> Tuple2.of(
                    a.getLocalName(),
                    ns -> ns.find(a.getValue())
                )
            );

        this.namespacePrefixes = namespacePrefixes;
        return this.namespacePrefixes;
    }

    public Result<Namespace, String> findNamespaceByPrefix( String prefix ){
        if( prefix==null ) throw new IllegalArgumentException("prefix==null");
        var nsPrefix = Result.of( getNsPrefixes().filter( pref -> prefix.equalsIgnoreCase(pref.getPrefix()) ).head(), "namespace prefix not found "+prefix );
        return nsPrefix.flatMap( pref -> getNamespaces().find(pref.getNamespace()));
    }
    //endregion
    //region namespaces : Namespaces
    private Namespaces namespaces;
    public Namespaces getNamespaces(){
        if( namespaces!=null )return namespaces;
        namespaces = new Namespaces( getNestedXsdDocs() );
        return namespaces;
    }
    //endregion

    private Result<Namespace,String> namespace;
    public Result<Namespace,String> getSelfNamespace(){
        if( namespace!=null )return namespace;
        return Result
            .of( getTargetNamespace(), "attribute targetNamespace not found")
            .map( ns -> new Namespace(ns, ImList.first(this)) );
    }

    private ImList<NsPrefix> nsPrefixes;
    public ImList<NsPrefix> getNsPrefixes(){
        if( nsPrefixes!=null )return nsPrefixes;
        nsPrefixes = elem().getAttributes().flatMap(NsPrefix::parseList);
        return nsPrefixes;
    }

    private Optional<String> version;
    public Optional<String> getVersion(){
        return elem().attrib("id").map(XmlAttr::getValue).head();
    }

    private ImList<XsdAnnotation> annotations;
    public ImList<XsdAnnotation> getAnnotations(){
        if( annotations!=null )return annotations;
        annotations = xmlDoc.getDocumentElement().getChildren().flatMap(n -> XsdAnnotation.parseList(n,this));
        return annotations;
    }

    private ImList<XsdElement> elements;
    public ImList<XsdElement> getElements(){
        if( elements!=null )return elements;
        elements = xmlDoc.getDocumentElement().getChildren().flatMap(n -> XsdElement.parseList(n,this));
        return elements;
    }

    private ImList<XsdAttribute> attributes;
    public ImList<XsdAttribute> getAttributes(){
        if( attributes!=null )return attributes;
        attributes = xmlDoc.getDocumentElement().getChildren().flatMap(n -> XsdAttribute.parseList(n,this));
        return attributes;
    }

    private ImList<XsdSimpleType> simpleTypes;
    public ImList<XsdSimpleType> getSimpleTypes(){
        if( simpleTypes!=null )return simpleTypes;
        simpleTypes = xmlDoc.getDocumentElement().getChildren().flatMap(n -> XsdSimpleType.parseList(n,this));
        return simpleTypes;
    }

    private ImList<XsdComplexType> complexTypes;
    public ImList<XsdComplexType> getComplexTypes(){
        if( complexTypes!=null )return complexTypes;
        complexTypes = xmlDoc.getDocumentElement().getChildren().flatMap(n -> XsdComplexType.parseList(n,this));
        return complexTypes;
    }

    private ImList<XsdGroup> groups;
    public ImList<XsdGroup> getGroups(){
        if( groups!=null )return groups;
        groups = xmlDoc.getDocumentElement().getChildren().flatMap(n -> XsdGroup.parseList(n,this));
        return groups;
    }

    private ImList<XsdAttributeGroup> attributeGroups;
    public ImList<XsdAttributeGroup> getAttributeGroups(){
        if( attributeGroups!=null )return attributeGroups;
        attributeGroups = xmlDoc.getDocumentElement().getChildren().flatMap(n -> XsdAttributeGroup.parseList(n,this));
        return attributeGroups;
    }

    private ImList<XsdNotation> notations;
    public ImList<XsdNotation> getNotations(){
        if( notations!=null )return notations;
        notations = xmlDoc.getDocumentElement().getChildren().flatMap(n -> XsdNotation.parseList(n,this));
        return notations;
    }

    private ImList<XsdRedefine> redefines;
    public ImList<XsdRedefine> getRedefines(){
        if( redefines!=null )return redefines;
        redefines = xmlDoc.getDocumentElement().getChildren().flatMap(n -> XsdRedefine.parseList(n,this));
        return redefines;
    }
}
