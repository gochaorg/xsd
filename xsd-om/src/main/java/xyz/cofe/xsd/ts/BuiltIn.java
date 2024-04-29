package xyz.cofe.xsd.ts;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.im.struct.Tuple2;
import xyz.cofe.ts.Primitive;
import xyz.cofe.ts.PrimitiveType;

import java.util.Map;
import java.util.Optional;

public class BuiltIn {
    public static final Primitive xsString = PrimitiveType.of("string");
    public static final Primitive xsDuration = PrimitiveType.of("duration");
    public static final Primitive xsDateTime = PrimitiveType.of("dateTime");
    public static final Primitive xsTime = PrimitiveType.of("time");
    public static final Primitive xsDate = PrimitiveType.of("date");
    public static final Primitive xsGYearMonth = PrimitiveType.of("gYearMonth");
    public static final Primitive xsGYear = PrimitiveType.of("gYear");
    public static final Primitive xsGMonthDay = PrimitiveType.of("gMonthDay");
    public static final Primitive xsGDay = PrimitiveType.of("gDay");
    public static final Primitive xsGMonth = PrimitiveType.of("gMonth");
    public static final Primitive xsBoolean = PrimitiveType.of("boolean");
    public static final Primitive xsBase64Binary = PrimitiveType.of("base64Binary");
    public static final Primitive xsHexBinary = PrimitiveType.of("hexBinary");
    public static final Primitive xsFloat = PrimitiveType.of("float");
    public static final Primitive xsDecimal = PrimitiveType.of("decimal");
    public static final Primitive xsDouble = PrimitiveType.of("double");
    public static final Primitive xsAnyURI = PrimitiveType.of("anyURI");
    public static final Primitive xsQName = PrimitiveType.of("QName");
    public static final Primitive xsNOTATION = PrimitiveType.of("NOTATION");

    public static final Primitive xsNormalizedString = PrimitiveType.of("normalizedString");
    public static final Primitive xsToken = PrimitiveType.of("token");
    public static final Primitive xsLanguage = PrimitiveType.of("language");
    public static final Primitive xsName = PrimitiveType.of("Name");
    public static final Primitive xsID = PrimitiveType.of("ID");
    public static final Primitive xsNMTOKEN = PrimitiveType.of("NMTOKEN");
    public static final Primitive xsNMTOKENS = PrimitiveType.of("NMTOKENS");
    public static final Primitive xsIDREF = PrimitiveType.of("IDREF");
    public static final Primitive xsIDREFS = PrimitiveType.of("IDREFS");
    public static final Primitive xsENTITY = PrimitiveType.of("ENTITY");
    public static final Primitive xsENTITIES = PrimitiveType.of("ENTITIES");

    public static final Primitive xsInteger = PrimitiveType.of("integer");
    public static final Primitive xsLong = PrimitiveType.of("long");
    public static final Primitive xsShort = PrimitiveType.of("short");
    public static final Primitive xsByte = PrimitiveType.of("byte");

    public static final Primitive xsNonPositiveInteger = PrimitiveType.of("nonPositiveInteger");
    public static final Primitive xsNegativeInteger = PrimitiveType.of("negativeInteger");

    public static final Primitive xsNonNegativeInteger = PrimitiveType.of("nonNegativeInteger");
    public static final Primitive xsPositiveInteger = PrimitiveType.of("positiveInteger");

    public static final Primitive xsUnsignedLong = PrimitiveType.of("unsignedLong");
    public static final Primitive xsUnsignedInt = PrimitiveType.of("unsignedInt");
    public static final Primitive xsUnsignedShort = PrimitiveType.of("unsignedShort");
    public static final Primitive xsUnsignedByte = PrimitiveType.of("unsignedByte");

    public static final ImList<Primitive> primitives =
        ImList.first(xsString).prepend(xsDuration).prepend(xsDateTime).prepend(xsTime).prepend(xsDate).prepend(xsGYearMonth).prepend(xsGYear).prepend(xsGMonthDay)
            .prepend(xsGDay).prepend(xsGMonth).prepend(xsBoolean).prepend(xsBase64Binary).prepend(xsHexBinary).prepend(xsFloat).prepend(xsDecimal).prepend(xsDouble)
            .prepend(xsAnyURI).prepend(xsQName).prepend(xsNOTATION).prepend(xsNormalizedString).prepend(xsToken).prepend(xsLanguage).prepend(xsName).prepend(xsID)
            .prepend(xsNMTOKEN).prepend(xsNMTOKENS).prepend(xsIDREF).prepend(xsIDREFS).prepend(xsENTITY).prepend(xsENTITIES).prepend(xsInteger).prepend(xsLong)
            .prepend(xsShort).prepend(xsByte).prepend(xsNonPositiveInteger).prepend(xsNegativeInteger).prepend(xsNonNegativeInteger).prepend(xsPositiveInteger)
            .prepend(xsUnsignedLong).prepend(xsUnsignedInt).prepend(xsUnsignedShort).prepend(xsUnsignedByte);

    private static Map<String, Primitive> nameMap;
    private static Map<String, Primitive> getNameMap(){
        if( nameMap!=null )return nameMap;
        nameMap = primitives.toMap(p -> Tuple2.of(p.getTypeName(), p));
        return nameMap;
    }

    public static Optional<Primitive> findByName(String name){
        if( name==null ) throw new IllegalArgumentException("name==null");
        return Optional.ofNullable(getNameMap().get(name));
    }
}
