package xyz.cofe.xsd.om;

import xyz.cofe.coll.im.Result;
import xyz.cofe.xml.XmlAttr;

public interface TypeAttribute extends ElemMethod {
    public default Result<BuiltInTypes.QName, String> getType() {
        return Result.from(
            elem().attrib("type").map(XmlAttr::getValue).head(),
            ()->"type not found"
        ).fmap(BuiltInTypes.QName::parse);
    }
}
