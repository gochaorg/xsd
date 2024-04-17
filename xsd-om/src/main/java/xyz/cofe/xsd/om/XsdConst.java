package xyz.cofe.xsd.om;

import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;

import java.util.Objects;

public class XsdConst {
    public static final String XMLSchemaNamespace = "http://www.w3.org/2001/XMLSchema";

    public static final String Schema = "schema";

    public static boolean isSchema(XmlNode node) {
        return
            node instanceof XmlElem el &&
                Objects.equals(el.getNamespaceURI(), XMLSchemaNamespace) &&
                Objects.equals(el.getLocalName(), Schema);
    }
}
