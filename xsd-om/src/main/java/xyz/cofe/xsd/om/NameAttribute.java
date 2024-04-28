package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.Result;
import xyz.cofe.xml.XmlAttr;

public interface NameAttribute extends ElemMethod {
    public default Result<BuiltInTypes.NCNAME, String> getName() {
        return Result.of(
            elem().attrib("name").map(XmlAttr::getValue).head(),
            "name not found"
        ).flatMap(BuiltInTypes.NCNAME::parse);
    }
}
