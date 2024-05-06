package xyz.cofe.xsd.ui.json;

import java.util.Optional;

public enum TypeOf {
    STRING,
    UNDEF,
    OBJECT,
    BOOLEAN,
    NUMBER,
    BIGINT,
    SYMBOL,
    FUNCTION;

    public static Optional<TypeOf> of(Object obj){
        if( obj==null )return Optional.empty();

        var str = JS.typeof(obj);
        if( str==null )return Optional.empty();

        if( str.equalsIgnoreCase("undefined") )return Optional.of(UNDEF);
        if( str.equalsIgnoreCase("object") )return Optional.of(OBJECT);
        if( str.equalsIgnoreCase("boolean") )return Optional.of(BOOLEAN);
        if( str.equalsIgnoreCase("number") )return Optional.of(NUMBER);
        if( str.equalsIgnoreCase("bigint") )return Optional.of(BIGINT);
        if( str.equalsIgnoreCase("string") )return Optional.of(STRING);
        if( str.equalsIgnoreCase("symbol") )return Optional.of(SYMBOL);
        if( str.equalsIgnoreCase("function") )return Optional.of(FUNCTION);

        return Optional.empty();
    }
}
