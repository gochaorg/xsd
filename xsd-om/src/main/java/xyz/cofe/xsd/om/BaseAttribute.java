package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.Result;
import xyz.cofe.xml.XmlAttr;

public interface BaseAttribute extends ElemMethod {
    public default Result<BuiltInTypes.QName, String> getBase() {
        return Result.of(
            elem().attrib("base").map(XmlAttr::getValue).head(),
            "base not found"
        ).flatMap(BuiltInTypes.QName::parse);
    }
}
