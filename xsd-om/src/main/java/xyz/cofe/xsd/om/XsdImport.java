package xyz.cofe.xsd.om;

import xyz.cofe.xsd.om.xml.XmlElem;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class XsdImport {
    public XsdImport(Optional<String> schemaLocation, Optional<String> namespace) {
        this.schemaLocation = schemaLocation;
        this.namespace = namespace;
    }

    //region schemaLocation : Optional<String>
    private Optional<String> schemaLocation;

    public Optional<String> getSchemaLocation() {
        return schemaLocation;
    }

    public void setSchemaLocation(Optional<String> schemaLocation) {
        this.schemaLocation = schemaLocation;
    }
    //endregion

    //region namespace : Optional<String>
    private Optional<String> namespace;

    public Optional<String> getNamespace() {
        return namespace;
    }

    public void setNamespace(Optional<String> namespace) {
        this.namespace = namespace;
    }
    //endregion

    public static Optional<XsdImport> parse(XmlElem elem){
        if( elem==null ) throw new IllegalArgumentException("elem==null");
        if( !XsdConst.XMLSchemaNamespace.equals(elem.getNamespaceURI()) )return Optional.empty();
        if( !XsdConst.Import.equals(elem.getLocalName()) )return Optional.empty();

        return Optional.of(
            new XsdImport(
                Optional.ofNullable(elem.getAttribute("schemaLocation")),
                Optional.ofNullable(elem.getAttribute("namespace"))
            )
        );
    }
}
