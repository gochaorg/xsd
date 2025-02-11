package xyz.cofe.xsd.om;

import xyz.cofe.xml.XmlElem;
import xyz.cofe.xml.XmlNode;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static xyz.cofe.xsd.om.Const.XMLSchemaNamespace;

/*
https://www.w3schools.com/xml/el_include.asp

<include
id=ID
schemaLocation=anyURI
any attributes
>

(annotation?)

</include>
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class XsdInclude implements SchemaLocation, Xsd {
    @SuppressWarnings("OptionalAssignedToNull")
    public XsdInclude(XmlElem elem, Optional<String> schemaLocation, Xsd parent) {
        if( elem==null ) throw new IllegalArgumentException("elem==null");
        if( schemaLocation==null ) throw new IllegalArgumentException("schemaLocation==null");
        this.schemaLocation = schemaLocation;
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
    //endregion

    private Map<URI, LinkedDoc> xsdDocs = new HashMap<>();
    public Map<URI, LinkedDoc> getXsdDocs() {
        return xsdDocs;
    }

    public static Optional<XsdInclude> parse(XmlElem elem, Xsd parent){
        if( elem==null ) throw new IllegalArgumentException("elem==null");
        if( !XMLSchemaNamespace.equals(elem.getNamespaceURI()) )return Optional.empty();
        if( !Include.equals(elem.getLocalName()) )return Optional.empty();

        return Optional.of(
            new XsdInclude( elem, Optional.ofNullable(elem.getAttribute("schemaLocation")), parent )
        );
    }

    public static final String Include = "include";

    public static boolean isInclude(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Include);
    }
}
