package xyz.cofe.xsd.om;

import xyz.cofe.coll.im.Result;
import xyz.cofe.xml.XmlAttr;

public interface RefAttribute extends ElemMethod {
    public default Result<BuiltInTypes.QName, String> getRef() {
        return Result.from(
            elem().attrib("ref").map(XmlAttr::getValue).head(),
            ()->"ref not found"
        ).fmap(BuiltInTypes.QName::parse);
    }
}
