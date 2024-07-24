package xyz.cofe.xsd.om;

import xyz.cofe.coll.im.Result;
import xyz.cofe.xml.XmlAttr;

public interface MinOccursAttribute extends ElemMethod {
    public default Result<BuiltInTypes.NON_NEGATIVE_INTEGER,String> getMinOccurs() {
        return Result.from(elem().attrib("minOccurs")
                .map(XmlAttr::getValue).head(),()->"minOccurs")
            .fmap(BuiltInTypes.NON_NEGATIVE_INTEGER::parse);
    }
}
