package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.Result;
import xyz.cofe.xml.XmlAttr;

public interface IDAttribute extends ElemMethod {
    public default Result<BuiltInTypes.ID, String> getId() {
        return Result.of(
            elem().attrib("id").map(XmlAttr::getValue).head(),
            "id not found"
        ).flatMap(BuiltInTypes.ID::parse);
    }
}
