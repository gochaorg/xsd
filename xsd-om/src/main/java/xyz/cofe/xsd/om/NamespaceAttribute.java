package xyz.cofe.xsd.om;

import xyz.cofe.coll.im.Result;
import xyz.cofe.xml.XmlAttr;

public interface NamespaceAttribute extends ElemMethod {
    public default Result<String, String> getNamespace() {
        return Result.from(
            elem().attrib("namespace").map(XmlAttr::getValue).head(),
            ()->"namespace not found"
        );
    }
}
