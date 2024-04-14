package xyz.cofe.xsd.http.fproc;

import java.util.function.Supplier;
import java.util.regex.Pattern;

public sealed interface StringFn {
    String apply(String str);

    record One() implements StringFn {
        @Override
        public String apply(String str) {
            return str;
        }
    }

    record InsertLineAfterFn(Supplier<Pattern> pattern, Supplier<String> injection) implements StringFn {
        @Override
        public String apply(String str) {
            String[] lines = str.split("\\r?\\n");
            StringBuffer buff = new StringBuffer();
            Pattern ptrn = pattern.get();
            String inj = injection.get();
            for( var line : lines ){
                if( !buff.isEmpty() )buff.append("\n");
                buff.append(line);
                if( ptrn.matcher(line).matches() ){
                    buff.append(inj);
                }
            }
            return buff.toString();
        }
    }
}
