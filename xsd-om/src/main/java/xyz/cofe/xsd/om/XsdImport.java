package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class XsdImport implements XsdSchemaLocation, Xsd {
    @SuppressWarnings("OptionalAssignedToNull")
    public XsdImport(Optional<String> schemaLocation, Optional<String> namespace) {
        if( schemaLocation==null ) throw new IllegalArgumentException("schemaLocation==null");
        if( namespace==null ) throw new IllegalArgumentException("namespace==null");
        this.schemaLocation = schemaLocation;
        this.namespace = namespace;
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

    public static Optional<XsdImport> parse(XmlElem elem){
        if( elem==null ) throw new IllegalArgumentException("elem==null");
        if( !XsdConst.XMLSchemaNamespace.equals(elem.getNamespaceURI()) )return Optional.empty();
        if( !Import.equals(elem.getLocalName()) )return Optional.empty();

        return Optional.of(
            new XsdImport(
                Optional.ofNullable(elem.getAttribute("schemaLocation")),
                Optional.ofNullable(elem.getAttribute("namespace"))
            )
        );
    }

    public static final String Import = "import";

    public static boolean isImport(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), XsdConst.XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Import);
    }
}
