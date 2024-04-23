package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.Result;
import xyz.cofe.xsd.om.xml.XmlAttr;

public interface NamespaceAttribute extends ElemMethod {
    public default Result<String, String> getNamespace() {
        return Result.of(
            elem().attrib("namespace").map(XmlAttr::getValue).head(),
            "namespace not found"
        );
    }
}
