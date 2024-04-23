package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.Result;
import xyz.cofe.xsd.om.xml.XmlAttr;

public interface MinOccursAttribute extends ElemMethod {
    public default Result<BuiltInTypes.NON_NEGATIVE_INTEGER,String> getMinOccurs() {
        return Result.of(elem().attrib("minOccurs")
                .map(XmlAttr::getValue).head(),"minOccurs")
            .flatMap(BuiltInTypes.NON_NEGATIVE_INTEGER::parse);
    }
}
