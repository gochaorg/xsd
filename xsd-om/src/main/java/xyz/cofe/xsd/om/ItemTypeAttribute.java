package xyz.cofe.xsd.om;

import xyz.cofe.coll.im.Result;
import xyz.cofe.xml.XmlAttr;

public interface ItemTypeAttribute extends ElemMethod {
    public default Result<BuiltInTypes.QName, String> getItemType() {
        return Result.from(
            elem().attrib("itemType").map(XmlAttr::getValue).head(),
            ()->"itemType not found"
        ).fmap(BuiltInTypes.QName::parse);
    }
}
