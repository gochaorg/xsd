package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.Result;
import xyz.cofe.xsd.om.xml.XmlAttr;

public interface TypeAttribute extends ElemMethod {
    public default Result<BuiltInTypes.QName, String> getType() {
        return Result.of(
            elem().attrib("type").map(XmlAttr::getValue).head(),
            "type not found"
        ).flatMap(BuiltInTypes.QName::parse);
    }
}
