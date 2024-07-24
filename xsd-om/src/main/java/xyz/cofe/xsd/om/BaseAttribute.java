package xyz.cofe.xsd.om;

import xyz.cofe.coll.im.Result;
import xyz.cofe.xml.XmlAttr;

import java.util.Optional;

public interface BaseAttribute extends ElemMethod {
    public default Result<BuiltInTypes.QName, String> getBase() {
        return Result.from(
            elem().attrib("base").map(XmlAttr::getValue).head(),
            ()->"base not found"
        ).fmap(BuiltInTypes.QName::parse);
    }

    public default Optional<XmlAttr> getBaseAttribute(){
        return elem().attrib("base").head();
    }
}
