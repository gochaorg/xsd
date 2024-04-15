package xyz.cofe.xsd.om;

import xyz.cofe.xsd.om.xml.XmlElem;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class XsdInclude {
    public XsdInclude(Optional<String> schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    private Optional<String> schemaLocation;

    public Optional<String> getSchemaLocation() {
        return schemaLocation;
    }

    public void setSchemaLocation(Optional<String> schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    public static Optional<XsdInclude> parse(XmlElem elem){
        if( elem==null ) throw new IllegalArgumentException("elem==null");
        if( !XsdConst.XMLSchemaNamespace.equals(elem.getNamespaceURI()) )return Optional.empty();
        if( !XsdConst.Include.equals(elem.getLocalName()) )return Optional.empty();

        return Optional.of(
            new XsdInclude( Optional.ofNullable(elem.getAttribute("schemaLocation")) )
        );
    }
}
