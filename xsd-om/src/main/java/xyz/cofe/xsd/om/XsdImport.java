package xyz.cofe.xsd.om;

import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/*
https://www.w3schools.com/xml/el_import.asp

<import
id=ID
namespace=anyURI
schemaLocation=anyURI
any attributes
>

(annotation?)

</import>
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class XsdImport implements SchemaLocation, Xsd {
    @SuppressWarnings("OptionalAssignedToNull")
    public XsdImport(XmlElem elem, Optional<String> schemaLocation, Optional<String> namespace, Xsd parent) {
        if( elem==null ) throw new IllegalArgumentException("elem==null");
        if( schemaLocation==null ) throw new IllegalArgumentException("schemaLocation==null");
        if( namespace==null ) throw new IllegalArgumentException("namespace==null");
        this.schemaLocation = schemaLocation;
        this.namespace = namespace;
        this.elem = elem;
        this.parent = Optional.ofNullable(parent);
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

    //region schemaLocation : Optional<String>
    private final Optional<String> schemaLocation;

    public Optional<String> getSchemaLocation() {
        return schemaLocation;
    }

//    public void setSchemaLocation(Optional<String> schemaLocation) {
//        this.schemaLocation = schemaLocation;
//    }
    //endregion

    //region namespace : Optional<String>
    private final Optional<String> namespace;

    public Optional<String> getNamespace() {
        return namespace;
    }

//    public void setNamespace(Optional<String> namespace) {
//        this.namespace = namespace;
//    }
    //endregion

    private final Map<URI, LinkedDoc> xsdDocs = new HashMap<>();
    public Map<URI, LinkedDoc> getXsdDocs() {
        return xsdDocs;
    }

    public static Optional<XsdImport> parse(XmlElem elem, Xsd parent){
        if( elem==null ) throw new IllegalArgumentException("elem==null");
        if( !Const.XMLSchemaNamespace.equals(elem.getNamespaceURI()) )return Optional.empty();
        if( !Import.equals(elem.getLocalName()) )return Optional.empty();

        return Optional.of(
            new XsdImport(
                elem,
                Optional.ofNullable(elem.getAttribute("schemaLocation")),
                Optional.ofNullable(elem.getAttribute("namespace")),
                parent
            )
        );
    }

    public static final String Import = "import";

    public static boolean isImport(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), Const.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Import);
    }
}
