package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.Result;
import xyz.cofe.xml.XmlAttr;

public interface RefAttribute extends ElemMethod {
    public default Result<BuiltInTypes.QName, String> getRef() {
        return Result.of(
            elem().attrib("ref").map(XmlAttr::getValue).head(),
            "ref not found"
        ).flatMap(BuiltInTypes.QName::parse);
    }
}
