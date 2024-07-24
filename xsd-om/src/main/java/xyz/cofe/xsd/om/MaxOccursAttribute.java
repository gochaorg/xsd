package xyz.cofe.xsd.om;

import xyz.cofe.coll.im.Either;
import xyz.cofe.coll.im.Result;
import xyz.cofe.xml.XmlAttr;

public interface MaxOccursAttribute extends ElemMethod {
    public default Result<Either<BuiltInTypes.NON_NEGATIVE_INTEGER,Unbounded>,String> getMaxOccurs() {
        return Result.from(
            elem().attrib("maxOccurs").map(XmlAttr::getValue).head(),()->"maxOccurs"
        ).fmap( s -> {
                if( s.equalsIgnoreCase("unbounded"))
                    return Result.ok(Either.right(Unbounded.instance));
                return BuiltInTypes.NON_NEGATIVE_INTEGER.parse(s).map(Either::left);
            });
    }
}
