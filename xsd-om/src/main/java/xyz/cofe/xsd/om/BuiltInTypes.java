package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.Result;
import xyz.cofe.im.struct.Tuple2;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

import static xyz.cofe.im.struct.Result.*;

// https://www.w3.org/TR/xmlschema-2/#built-in-datatypes
public sealed interface BuiltInTypes {
    public sealed interface AnySimpleType {}
    public sealed interface Numeric extends AnySimpleType {}
    public sealed interface IntegerNum extends Numeric {}
    public sealed interface LongNum extends IntegerNum {}
    public sealed interface UnsignedLongNum extends IntegerNum {}
    public sealed interface UnsignedIntNum extends UnsignedLongNum {}
    public sealed interface UnsignedShortNum extends UnsignedIntNum {}
    public sealed interface UnsignedByteNum extends UnsignedShortNum {}

    // https://www.w3.org/TR/1999/REC-xml-names-19990114/#NT-NCName
    private static boolean isNCNameChar(char chr){ return Const.isLetter(chr) || Const.isDigit(chr) || chr=='.'  || chr=='-' || chr=='_' || Const.isCombiningChar(chr) || Const.isExtender(chr); }

    private static boolean isNCName(String text){
        if(text==null || text.isEmpty())return false;
        if( !(Const.isLetter(text.charAt(0)) || (text.charAt(0)=='_')) )return false;
        for( int i=1;i<text.length();i++ ){
            if( !isNCNameChar(text.charAt(i)))return false;
        }
        return true;
    }

    record STRING(String value) implements BuiltInTypes, AnySimpleType {
        public static Result<STRING,String> parse(String str){
            if(str==null) return err("null value");
            return ok(new STRING(str));
        }
    }

    record NORMALIZED_STRING(String value) implements BuiltInTypes {
        public static Result<NORMALIZED_STRING,String> parse(String str){
            return STRING.parse(str).flatMap(v ->
                v.value().contains("\r") || v.value().contains("\n") || v.value().contains("\t")
                ? err("normalized string must do not contains \\r | \\n | \\t")
                : ok(new NORMALIZED_STRING(v.value))
            );
        }
    }

    record NCNAME(String value) implements BuiltInTypes {
        public static Result<NCNAME,String> parse(String value){
            return NORMALIZED_STRING.parse(value)
                .flatMap(v ->
                    BuiltInTypes.isNCName(v.value())
                    ? ok(new NCNAME(v.value)) : err("value "+v.value+" is not NCName, see https://www.w3.org/TR/1999/REC-xml-names-19990114/#NT-NCName")
                );
        }

        public static Optional<Tuple2<NCNAME,Integer>> parse(String value,int offset){
            if( value==null ) throw new IllegalArgumentException("value==null");

            if( offset<0 ) throw new IllegalArgumentException("offset<0");
            if( offset>=value.length() ) return Optional.empty();

            var ptr = offset;
            if( !(Const.isLetter(value.charAt(ptr)) || (value.charAt(ptr)=='_')) )return Optional.empty();

            ptr++;
            while (ptr < value.length()){
                if( isNCNameChar(value.charAt(ptr)) ){
                    ptr++;
                    continue;
                }
                break;
            }

            return Optional.of(
                Tuple2.of(
                    new NCNAME(value.substring(offset,ptr)),
                    ptr
                )
            );
        }
    }

    record ID(String value) implements BuiltInTypes {
        public static Result<ID,String> parse(String value){
            return NCNAME.parse(value).map(v -> new ID(v.value));
        }
    }

    record ENTITY(String value) implements BuiltInTypes {
        public static Result<ENTITY,String> parse(String value){
            return NCNAME.parse(value).map(v -> new ENTITY(v.value));
        }
    }

    record FLOAT(float value,String string) implements BuiltInTypes, AnySimpleType {
        public static Result<FLOAT,String> parse(String value){
            if(value==null) return err("null value");
            try {
                return ok(new FLOAT(Float.parseFloat(value),value));
            } catch (NumberFormatException ex) {
                return err("NumberFormatException:" +
                    " source:" + value +
                    " message:" + ex.getMessage());
            }
        }
    }

    record DOUBLE(double value,String string) implements BuiltInTypes, AnySimpleType {
        public static Result<DOUBLE,String> parse(String value){
            if(value==null) return err("null value");
            try {
                return ok(new DOUBLE(Double.parseDouble(value),value));
            } catch (NumberFormatException ex) {
                return err("NumberFormatException:" +
                    " source:" + value +
                    " message:" + ex.getMessage());
            }
        }
    }

    record BOOLEAN(boolean value) implements BuiltInTypes, AnySimpleType {
        public static Result<BOOLEAN, String> parse(String value) {
            if (value == null) return err("null value");
            if (value.equals("true") || value.equals("1")) return ok(new BOOLEAN(true));
            if (value.equals("false") || value.equals("0")) return ok(new BOOLEAN(false));
            return err("unexpected value: " + value);
        }
    }

    // https://en.wikipedia.org/wiki/ISO_8601
    // PnYnMnDTnHnMnS
    // PnW
    // P<date>T<time>
    record Duration(
        String string
    ) implements BuiltInTypes, AnySimpleType {
        public static Result<Duration, String> parse(String value) {
            return STRING.parse(value).map(v -> new Duration(v.value()));
        }
    }

    record DateTime(
        String string
    ) implements BuiltInTypes, AnySimpleType {
        public static Result<DateTime, String> parse(String value) {
            return STRING.parse(value).map(v -> new DateTime(v.value()));
        }
    }

    record Time(
        String string
    ) implements BuiltInTypes, AnySimpleType {
        public static Result<Time, String> parse(String value) {
            return STRING.parse(value).map(v -> new Time(v.value()));
        }
    }

    record Date(
        String string
    ) implements BuiltInTypes, AnySimpleType {
        public static Result<Date, String> parse(String value) {
            return STRING.parse(value).map(v -> new Date(v.value()));
        }
    }

    record GYearMonth(
        String string
    ) implements BuiltInTypes, AnySimpleType {
        public static Result<GYearMonth, String> parse(String value) {
            return STRING.parse(value).map(v -> new GYearMonth(v.value()));
        }
    }

    record GYear(
        String string
    ) implements BuiltInTypes, AnySimpleType {
        public static Result<GYear, String> parse(String value) {
            return STRING.parse(value).map(v -> new GYear(v.value()));
        }
    }

    record GMonthDay(
        String string
    ) implements BuiltInTypes, AnySimpleType {
        public static Result<GMonthDay, String> parse(String value) {
            return STRING.parse(value).map(v -> new GMonthDay(v.value()));
        }
    }

    record GDay(
        String string
    ) implements BuiltInTypes, AnySimpleType {
        public static Result<GDay, String> parse(String value) {
            return STRING.parse(value).map(v -> new GDay(v.value()));
        }
    }

    record GMonth(
        String string
    ) implements BuiltInTypes, AnySimpleType {
        public static Result<GMonth, String> parse(String value) {
            return STRING.parse(value).map(v -> new GMonth(v.value()));
        }
    }

    record Base64Binary(
        String string
    ) implements BuiltInTypes, AnySimpleType {
        public static Result<Base64Binary, String> parse(String value) {
            return STRING.parse(value).map(v -> new Base64Binary(v.value()));
        }
    }

    record HexBinary(
        String string
    ) implements BuiltInTypes, AnySimpleType {
        public static Result<HexBinary, String> parse(String value) {
            return STRING.parse(value).map(v -> new HexBinary(v.value()));
        }
    }

    record AnyURI(
        String string
    ) implements BuiltInTypes, AnySimpleType {
        public static Result<AnyURI, String> parse(String value) {
            return STRING.parse(value).map(v -> new AnyURI(v.value()));
        }
    }

    // https://www.w3.org/TR/1999/REC-xml-names-19990114/#dt-qname
    record QName(
        Optional<String> prefix,
        String localPart
    ) implements BuiltInTypes, AnySimpleType {
        public static Result<QName, String> parse(String value) {
            return STRING.parse(value).flatMap(v -> {
                var str = v.value();

                var p1opt = NCNAME.parse(str,0);
                if(p1opt.isEmpty())return err("can't parse NCName from \""+v.value()+"\" offset=0");

                var p1tup = p1opt.get();
                var p1 = p1tup.a();
                var off1 = p1tup.b();

                String str2 = off1 >= str.length() ? "" : str.substring(off1);
                if( str2.isEmpty() )return ok(new QName(Optional.empty(), p1.value()));

                if( !str2.startsWith(":") )return err("expect begin ':', but found "+str2);
                if( str2.length()<2 )return err("expect length > 2, but found '"+str2+"'");

                String str3 = str2.substring(1);
                return NCNAME.parse(str3).map( p2 -> new QName(Optional.of(p1.value()), p2.value()));
            });
        }
    }

    record NOTATION(
        String string
    ) implements BuiltInTypes, AnySimpleType {
        public static Result<NOTATION, String> parse(String value) {
            return STRING.parse(value).map(v -> new NOTATION(v.value()));
        }
    }

    record Decimal(
        BigDecimal value,
        String string
    ) implements BuiltInTypes, Numeric {
        public static Result<Decimal, String> parse(String value) {
            if (value == null) return err("null value");
            try {
                return ok(new Decimal(new BigDecimal(value), value));
            } catch (NumberFormatException ex) {
                return err("NumberFormatException:" +
                    " source:" + value +
                    " message:" + ex.getMessage());
            }
        }
    }

    record INTEGER(
        BigInteger value,
        String string
    ) implements BuiltInTypes, IntegerNum {
        public static Result<INTEGER, String> parse(String value) {
            if (value == null) return err("null value");
            try {
                return ok(new INTEGER(new BigInteger(value), value));
            } catch (NumberFormatException ex) {
                return err("NumberFormatException:" +
                    " source:" + value +
                    " message:" + ex.getMessage());
            }
        }
    }

    record NON_POSITIVE_INTEGER(
        BigInteger value,
        String string
    ) implements BuiltInTypes, IntegerNum {
        public static Result<NON_POSITIVE_INTEGER, String> parse(String value) {
            return INTEGER.parse(value).flatMap(v ->
                v.value.compareTo(BigInteger.ZERO)<=0
                ? ok( new NON_POSITIVE_INTEGER(v.value, v.string))
                : err("value ("+v.value+") is positive")
            );
        }
    }

    record NEGATIVE_INTEGER(
        BigInteger value,
        String string
    ) implements BuiltInTypes, IntegerNum {
        public static Result<NEGATIVE_INTEGER, String> parse(String value) {
            return INTEGER.parse(value).flatMap(v ->
                v.value.compareTo(BigInteger.ZERO)<0
                ? ok( new NEGATIVE_INTEGER(v.value, v.string))
                : err("value ("+v.value+") is zero or positive")
            );
        }
    }

    record NON_NEGATIVE_INTEGER(
        BigInteger value,
        String string
    ) implements BuiltInTypes, IntegerNum {
        public static Result<NON_NEGATIVE_INTEGER, String> parse(String value) {
            return INTEGER.parse(value).flatMap(v ->
                v.value.compareTo(BigInteger.ZERO)>=0
                ? ok( new NON_NEGATIVE_INTEGER(v.value, v.string))
                : err("value ("+v.value+") is negative")
            );
        }
    }

    record POSTIVE_INTEGER(
        BigInteger value,
        String string
    ) implements BuiltInTypes, IntegerNum {
        public static Result<POSTIVE_INTEGER, String> parse(String value) {
            return INTEGER.parse(value).flatMap(v ->
                v.value.compareTo(BigInteger.ZERO)>0
                    ? ok( new POSTIVE_INTEGER(v.value, v.string))
                    : err("value ("+v.value+") is zero or negative")
            );
        }
    }

    record LONG(long value, String string) implements BuiltInTypes, LongNum {
        public static Result<LONG, String> parse(String value) {
            if (value == null) return err("null value");
            try {
                return ok(new LONG(Long.parseLong(value), value));
            } catch (NumberFormatException ex) {
                return err("NumberFormatException:" +
                    " source:" + value +
                    " message:" + ex.getMessage());
            }
        }
    }

    record INT(int value, String string) implements BuiltInTypes, LongNum {
        public static Result<INT, String> parse(String value) {
            if (value == null) return err("null value");
            try {
                return ok(new INT(Integer.parseInt(value), value));
            } catch (NumberFormatException ex) {
                return err("NumberFormatException:" +
                    " source:" + value +
                    " message:" + ex.getMessage());
            }
        }
    }

    record SHORT(short value, String string) implements BuiltInTypes, LongNum {
        public static Result<SHORT, String> parse(String value) {
            if (value == null) return err("null value");
            try {
                return ok(new SHORT(Short.parseShort(value), value));
            } catch (NumberFormatException ex) {
                return err("NumberFormatException:" +
                    " source:" + value +
                    " message:" + ex.getMessage());
            }
        }
    }

    record BYTE(byte value, String string) implements BuiltInTypes, LongNum {
        public static Result<BYTE, String> parse(String value) {
            if (value == null) return err("null value");
            try {
                return ok(new BYTE(Byte.parseByte(value), value));
            } catch (NumberFormatException ex) {
                return err("NumberFormatException:" +
                    " source:" + value +
                    " message:" + ex.getMessage());
            }
        }
    }

    record UNSIGNED_LONG(BigInteger value, String string) implements BuiltInTypes, UnsignedLongNum {
        public static Result<UNSIGNED_LONG, String> parse(String value) {
            return NON_NEGATIVE_INTEGER.parse(value).flatMap(
                v -> v.value().compareTo(new BigInteger("18446744073709551615")) > 0
                ? err("value ("+value+") more than 18446744073709551615")
                : ok(new UNSIGNED_LONG(v.value, v.string))
            );
        }
    }

    record UNSIGNED_INT(long value, String string) implements BuiltInTypes, UnsignedIntNum {
        public static Result<UNSIGNED_INT, String> parse(String value) {
            return LONG.parse(value).flatMap(
                v -> v.value() > 4294967295L || v.value < 0
                    ? err("value ("+value+") more than 4294967295 or less than 0")
                    : ok(new UNSIGNED_INT(v.value, v.string))
            );
        }
    }

    record UNSIGNED_SHORT(int value, String string) implements BuiltInTypes, UnsignedShortNum {
        public static Result<UNSIGNED_SHORT, String> parse(String value) {
            return INT.parse(value).flatMap(
                v -> v.value > 65535 || v.value < 0
                    ? err("value ("+ v.value +") more than 65535 or less than 0")
                    : ok(new UNSIGNED_SHORT(v.value, v.string))
            );
        }
    }

    record UNSIGNED_BYTE(int value, String string) implements BuiltInTypes, UnsignedByteNum {
        public static Result<UNSIGNED_SHORT, String> parse(String value) {
            return INT.parse(value).flatMap(
                v -> v.value > 255 || v.value < 0
                    ? err("value ("+ v.value +") more than 255 or less than 0")
                    : ok(new UNSIGNED_SHORT(v.value, v.string))
            );
        }
    }
}
