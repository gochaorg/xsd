package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.Either;
import xyz.cofe.im.struct.Result;
import xyz.cofe.xsd.om.xml.XmlAttr;

public interface MaxOccursAttribute extends ElemMethod {
    public default Result<Either<BuiltInTypes.NON_NEGATIVE_INTEGER,Unbounded>,String> getMaxOccurs() {
        return Result.of(elem().attrib("maxOccurs")
                .map(XmlAttr::getValue).head(),"maxOccurs")
            .flatMap( s -> {
                if( s.equalsIgnoreCase("unbounded"))
                    return Result.ok(Either.right(Unbounded.instance));
                return BuiltInTypes.NON_NEGATIVE_INTEGER.parse(s).map(Either::left);
            });
    }
}
