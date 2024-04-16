package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.xsd.om.xml.XmlAttr;
import xyz.cofe.xsd.om.xml.XmlDoc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "OptionalAssignedToNull"})
public class XsdDoc {
    public final XmlDoc xmlDoc;

    public XsdDoc(XmlDoc xmlDoc) {
        if( xmlDoc==null ) throw new IllegalArgumentException("xmlDoc==null");
        this.xmlDoc = xmlDoc;
    }

    //region includes : List<XsdInclude>
    private List<XsdInclude> includes;
    public List<XsdInclude> getIncludes(){
        if( includes!=null )return includes;
        this.includes =
            xmlDoc.getDocumentElement().walk().elems()
                .flatMap( elem ->
                    XsdInclude.parse(elem)
                        .map(xsdInclude -> List.of(xsdInclude).iterator())
                        .orElse(Collections.emptyIterator()))
                .toList();
        return includes;
    }
    //endregion

    //region imports : List<XsdImport>
    private List<XsdImport> imports;
    public List<XsdImport> getImports(){
        if( imports!=null )return imports;
        this.imports =
            xmlDoc.getDocumentElement().walk().elems()
                .flatMap( elem ->
                    XsdImport.parse(elem)
                        .map(xsdInclude -> List.of(xsdInclude).iterator())
                        .orElse(Collections.emptyIterator()))
                .toList();
        return imports;
    }
    //endregion

    //region targetNamespace : Optional<String>
    private Optional<String> targetNamespace;

    public Optional<String> getTargetNamespace(){
        if( targetNamespace!=null )return targetNamespace;
        targetNamespace = xmlDoc.getDocumentElement().attrib("targetNamespace").head().map(XmlAttr::getValue);
        return targetNamespace;
    }
    //endregion

    public List<XsdDoc> getNestedXsdDocs(){
    }
}
