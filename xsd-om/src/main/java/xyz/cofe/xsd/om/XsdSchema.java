package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.im.struct.Result;
import xyz.cofe.im.struct.Tuple2;
import xyz.cofe.xsd.om.xml.XmlAttr;
import xyz.cofe.xsd.om.xml.XmlDoc;

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
public final class XsdSchema implements Xsd {
    public final XmlDoc xmlDoc;

    public XsdSchema(XmlDoc xmlDoc) {
        if (xmlDoc == null) throw new IllegalArgumentException("xmlDoc==null");
        this.xmlDoc = xmlDoc;
    }

    //region includes : List<XsdInclude>
    private List<XsdInclude> includes;

    public List<XsdInclude> getIncludes() {
        if (includes != null) return includes;
        this.includes =
            xmlDoc.getDocumentElement().walk().elems()
                .flatMap(elem ->
                    XsdInclude.parse(elem)
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
                    XsdImport.parse(elem)
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
    //endregion
    //region namespaces : Namespaces
    private Namespaces namespaces;
    public Namespaces getNamespaces(){
        if( namespaces!=null )return namespaces;
        namespaces = new Namespaces( getNestedXsdDocs() );
        return namespaces;
    }
    //endregion

    public ImList<XsdAnnotation> getAnnotations(){
        return xmlDoc.getDocumentElement().getChildren().flatMap(XsdAnnotation::parseList);
    }

    public ImList<XsdElement> getElements(){
        return xmlDoc.getDocumentElement().getChildren().flatMap(XsdElement::parseList);
    }

    public ImList<XsdAttribute> getAttributes(){
        return xmlDoc.getDocumentElement().getChildren().flatMap(XsdAttribute::parseList);
    }

    public ImList<XsdSimpleType> getSimpleTypes(){
        return xmlDoc.getDocumentElement().getChildren().flatMap(XsdSimpleType::parseList);
    }

    public ImList<XsdComplexType> getComplexTypes(){
        return xmlDoc.getDocumentElement().getChildren().flatMap(XsdComplexType::parseList);
    }

    public ImList<XsdGroup> getGroups(){
        return xmlDoc.getDocumentElement().getChildren().flatMap(XsdGroup::parseList);
    }

    public ImList<XsdAttributeGroup> getAttributeGroups(){
        return xmlDoc.getDocumentElement().getChildren().flatMap(XsdAttributeGroup::parseList);
    }

    public ImList<XsdNotation> getNotations(){
        return xmlDoc.getDocumentElement().getChildren().flatMap(XsdNotation::parseList);
    }

    public ImList<XsdRedefine> getRedefines(){
        return xmlDoc.getDocumentElement().getChildren().flatMap(XsdRedefine::parseList);
    }
}
