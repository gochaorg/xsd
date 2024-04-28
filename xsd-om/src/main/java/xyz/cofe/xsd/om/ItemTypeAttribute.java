package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.Result;
import xyz.cofe.xml.XmlAttr;

public interface ItemTypeAttribute extends ElemMethod {
    public default Result<BuiltInTypes.QName, String> getItemType() {
        return Result.of(
            elem().attrib("itemType").map(XmlAttr::getValue).head(),
            "itemType not found"
        ).flatMap(BuiltInTypes.QName::parse);
    }
}
