package xyz.cofe.xsd.text;

import xyz.cofe.coll.im.ImList;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.function.Consumer;

public class NewLine {
    public static ImList<String> split(String text){
        if( text==null ) throw new IllegalArgumentException("text==null");
        return ImList.from(Arrays.asList(text.split("\r?\n")));
    }

    public static class LineCapture {
        private final StringBuilder buffer = new StringBuilder();
        private int state = 0;
        private final Runnable lineFeed; // \n
        private final Runnable CRLF;
        private final Runnable NEL;
        private final Runnable lineSeparator;
        private final Runnable paragraphSeparator;
        private final Consumer<String> textConsumer;

        public LineCapture(Runnable lineFeed, Runnable CRLF, Runnable NEL, Runnable lineSeparator, Runnable paragraphSeparator, Consumer<String> textConsumer) {
            if( lineFeed==null ) throw new IllegalArgumentException("lineFeed==null");
            if( CRLF==null ) throw new IllegalArgumentException("CRLF==null");
            if( NEL==null ) throw new IllegalArgumentException("NEL==null");
            if( lineSeparator==null ) throw new IllegalArgumentException("lineSeparator==null");
            if( paragraphSeparator==null ) throw new IllegalArgumentException("paragraphSeparator==null");
            if( textConsumer==null ) throw new IllegalArgumentException("textConsumer==null");
            this.lineFeed = lineFeed;
            this.CRLF = CRLF;
            this.NEL = NEL;
            this.lineSeparator = lineSeparator;
            this.paragraphSeparator = paragraphSeparator;
            this.textConsumer = textConsumer;
        }

        public LineCapture(Consumer<String> textConsumer, Consumer<String> lineSeparator){
            if( textConsumer==null ) throw new IllegalArgumentException("textConsumer==null");
            if( lineSeparator==null ) throw new IllegalArgumentException("lineSeparator==null");
            this.textConsumer = textConsumer;
            this.lineSeparator = ()-> lineSeparator.accept("\u2028");
            this.lineFeed = ()-> lineSeparator.accept("\n");
            this.CRLF = ()-> lineSeparator.accept("\r\n");
            this.NEL = ()-> lineSeparator.accept("\u0085");
            this.paragraphSeparator = ()-> lineSeparator.accept("\u2029");
        }

        public LineCapture(Consumer<String> textConsumer){
            if( textConsumer==null ) throw new IllegalArgumentException("textConsumer==null");
            this.textConsumer = textConsumer;
            this.lineSeparator = ()->{};
            this.lineFeed = this.lineSeparator;
            this.CRLF = this.lineSeparator;
            this.NEL = this.lineSeparator;
            this.paragraphSeparator = this.lineSeparator;
        }

        public void write(char[] cbuf, int offset, int len) {
            for( var off = offset; off<(offset+len); off++ ){
                char chr = cbuf[off];
                switch (state) {
                    case 0 -> {
                        switch (chr){
                            case '\n' -> {
                                flush();
                                lineFeed.run();
                            }
                            case '\r' -> {
                                state = 1;
                            }
                            case '\u0085' -> {
                                NEL.run();
                            }
                            case '\u2028' -> {
                                lineSeparator.run();
                            }
                            case '\u2029' -> {
                                paragraphSeparator.run();
                            }
                            default -> {
                                buffer.append(chr);
                            }
                        }
                    }
                    case 1 -> {
                        //noinspection SwitchStatementWithTooFewBranches
                        switch (chr){
                            case '\n' -> {
                                state = 0;
                                flush();
                                CRLF.run();
                            }
                            default -> {
                                state = 0;
                                buffer.append(chr);
                            }
                        }
                    }
                }
            }
        }

        public void flush(){
            textConsumer.accept(buffer.toString());
            buffer.setLength(0);
        }
    }
}
