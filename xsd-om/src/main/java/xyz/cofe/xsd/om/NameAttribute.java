package xyz.cofe.xsd.om;

import xyz.cofe.coll.im.Result;
import xyz.cofe.xml.XmlAttr;

public interface NameAttribute extends ElemMethod {
    public default Result<BuiltInTypes.NCNAME, String> getName() {
        return Result.from(
            elem().attrib("name").map(XmlAttr::getValue).head(),
            ()->"name not found"
        ).fmap(BuiltInTypes.NCNAME::parse);
    }
}
