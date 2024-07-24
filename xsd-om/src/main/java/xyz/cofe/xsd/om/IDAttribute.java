package xyz.cofe.xsd.om;

import xyz.cofe.coll.im.Result;
import xyz.cofe.xml.XmlAttr;

public interface IDAttribute extends ElemMethod {
    public default Result<BuiltInTypes.ID, String> getId() {
        return Result.from(
            elem().attrib("id").map(XmlAttr::getValue).head(),
            ()->"id not found"
        ).fmap(BuiltInTypes.ID::parse);
    }
}
