package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.im.struct.Tuple2;
import xyz.cofe.xsd.om.xml.XmlElem;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class XsdInclude implements XsdSchemaLocation {
    public XsdInclude(Optional<String> schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    //region schemaLocation : Optional<String>
    private final Optional<String> schemaLocation;

    public Optional<String> getSchemaLocation() {
        return schemaLocation;
    }
    //endregion

    private Map<URI, LinkedDoc> xsdDocs = new HashMap<>();
    public Map<URI, LinkedDoc> getXsdDocs() {
        return xsdDocs;
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
