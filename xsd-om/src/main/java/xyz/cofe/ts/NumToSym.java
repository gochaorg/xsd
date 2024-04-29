package xyz.cofe.ts;

import java.util.function.Function;

/**
 * Преобразование числа в символ (0 -&gt; A, 1 -&gt; B, ...)
 */
public interface NumToSym extends Function<Integer,String> {
    String numToSym(int n);

    @Override
    default String apply(Integer n){
        return numToSym(n);
    }

    static class NumToSymBasic implements NumToSym {
        private static final String LETTERS = "abcdefghijklmnopqrstuvwxyz".toUpperCase();

        private static String numToWord(int n){
            n = Math.abs(n);

            int digitCount = LETTERS.length();
            StringBuilder buff = new StringBuilder();

            while (true) {
                if( n<digitCount ){
                    buff.insert(0,LETTERS.charAt(n));
                    break;
                }

                int m = n % digitCount;
                buff.insert(0,LETTERS.charAt(m));

                n = n / digitCount;
            }

            return buff.toString();
        }

        @Override
        public String numToSym(int n) {
            return numToWord(n);
        }
    }
}
