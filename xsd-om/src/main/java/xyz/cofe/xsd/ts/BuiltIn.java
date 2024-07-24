package xyz.cofe.xsd.ts;

import xyz.cofe.coll.im.ImList;
import xyz.cofe.coll.im.Tuple2;
import xyz.cofe.ts.PrimitiveType;

import java.util.Map;
import java.util.Optional;

public class BuiltIn {
    public static final PrimitiveType xsString = PrimitiveType.of("string");
    public static final PrimitiveType xsDuration = PrimitiveType.of("duration");
    public static final PrimitiveType xsDateTime = PrimitiveType.of("dateTime");
    public static final PrimitiveType xsTime = PrimitiveType.of("time");
    public static final PrimitiveType xsDate = PrimitiveType.of("date");
    public static final PrimitiveType xsGYearMonth = PrimitiveType.of("gYearMonth");
    public static final PrimitiveType xsGYear = PrimitiveType.of("gYear");
    public static final PrimitiveType xsGMonthDay = PrimitiveType.of("gMonthDay");
    public static final PrimitiveType xsGDay = PrimitiveType.of("gDay");
    public static final PrimitiveType xsGMonth = PrimitiveType.of("gMonth");
    public static final PrimitiveType xsBoolean = PrimitiveType.of("boolean");
    public static final PrimitiveType xsBase64Binary = PrimitiveType.of("base64Binary");
    public static final PrimitiveType xsHexBinary = PrimitiveType.of("hexBinary");
    public static final PrimitiveType xsFloat = PrimitiveType.of("float");
    public static final PrimitiveType xsDecimal = PrimitiveType.of("decimal");
    public static final PrimitiveType xsDouble = PrimitiveType.of("double");
    public static final PrimitiveType xsAnyURI = PrimitiveType.of("anyURI");
    public static final PrimitiveType xsQName = PrimitiveType.of("QName");
    public static final PrimitiveType xsNOTATION = PrimitiveType.of("NOTATION");

    public static final PrimitiveType xsNormalizedString = PrimitiveType.of("normalizedString");
    public static final PrimitiveType xsToken = PrimitiveType.of("token");
    public static final PrimitiveType xsLanguage = PrimitiveType.of("language");
    public static final PrimitiveType xsName = PrimitiveType.of("Name");
    public static final PrimitiveType xsID = PrimitiveType.of("ID");
    public static final PrimitiveType xsNMTOKEN = PrimitiveType.of("NMTOKEN");
    public static final PrimitiveType xsNMTOKENS = PrimitiveType.of("NMTOKENS");
    public static final PrimitiveType xsIDREF = PrimitiveType.of("IDREF");
    public static final PrimitiveType xsIDREFS = PrimitiveType.of("IDREFS");
    public static final PrimitiveType xsENTITY = PrimitiveType.of("ENTITY");
    public static final PrimitiveType xsENTITIES = PrimitiveType.of("ENTITIES");

    public static final PrimitiveType xsInteger = PrimitiveType.of("integer");
    public static final PrimitiveType xsLong = PrimitiveType.of("long");
    public static final PrimitiveType xsShort = PrimitiveType.of("short");
    public static final PrimitiveType xsByte = PrimitiveType.of("byte");

    public static final PrimitiveType xsNonPositiveInteger = PrimitiveType.of("nonPositiveInteger");
    public static final PrimitiveType xsNegativeInteger = PrimitiveType.of("negativeInteger");

    public static final PrimitiveType xsNonNegativeInteger = PrimitiveType.of("nonNegativeInteger");
    public static final PrimitiveType xsPositiveInteger = PrimitiveType.of("positiveInteger");

    public static final PrimitiveType xsUnsignedLong = PrimitiveType.of("unsignedLong");
    public static final PrimitiveType xsUnsignedInt = PrimitiveType.of("unsignedInt");
    public static final PrimitiveType xsUnsignedShort = PrimitiveType.of("unsignedShort");
    public static final PrimitiveType xsUnsignedByte = PrimitiveType.of("unsignedByte");

    public static final ImList<PrimitiveType> primitives =
        ImList.of(xsString).prepend(xsDuration).prepend(xsDateTime).prepend(xsTime).prepend(xsDate).prepend(xsGYearMonth).prepend(xsGYear).prepend(xsGMonthDay)
            .prepend(xsGDay).prepend(xsGMonth).prepend(xsBoolean).prepend(xsBase64Binary).prepend(xsHexBinary).prepend(xsFloat).prepend(xsDecimal).prepend(xsDouble)
            .prepend(xsAnyURI).prepend(xsQName).prepend(xsNOTATION).prepend(xsNormalizedString).prepend(xsToken).prepend(xsLanguage).prepend(xsName).prepend(xsID)
            .prepend(xsNMTOKEN).prepend(xsNMTOKENS).prepend(xsIDREF).prepend(xsIDREFS).prepend(xsENTITY).prepend(xsENTITIES).prepend(xsInteger).prepend(xsLong)
            .prepend(xsShort).prepend(xsByte).prepend(xsNonPositiveInteger).prepend(xsNegativeInteger).prepend(xsNonNegativeInteger).prepend(xsPositiveInteger)
            .prepend(xsUnsignedLong).prepend(xsUnsignedInt).prepend(xsUnsignedShort).prepend(xsUnsignedByte);

    private static Map<String, PrimitiveType> nameMap;
    private static Map<String, PrimitiveType> getNameMap(){
        if( nameMap!=null )return nameMap;
        nameMap = primitives.toMap(p -> Tuple2.of(p.getTypeName(), p));
        return nameMap;
    }

    public static Optional<PrimitiveType> findByName(String name){
        if( name==null ) throw new IllegalArgumentException("name==null");
        return Optional.ofNullable(getNameMap().get(name));
    }
}
