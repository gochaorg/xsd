package xyz.cofe.xsd.text;

import xyz.cofe.coll.im.Tuple2;

import java.io.IOError;
import java.io.IOException;
import java.io.Writer;
import java.util.function.Consumer;
import java.util.function.Function;

public class Indent {
    public static String indent(String ident, String text) {
        if (ident == null) throw new IllegalArgumentException("ident==null");
        if (text == null) throw new IllegalArgumentException("text==null");
        return NewLine.split(text)
            .map(line -> ident + line)
            .foldLeft(
                Tuple2.of("", 0),
                (acc, it) -> Tuple2.of(acc._1() + (acc._2()>0 ? "\n" : "") + it, acc._2() + 1)
            )._1();
    }

    public static abstract class BaseLineReWriter<SELF extends BaseLineReWriter<SELF>> extends Writer {
        private final Writer target;
        private final NewLine.LineCapture lineCapture;
        private Function<String,String> reWriter;

        public BaseLineReWriter(Writer target, Function<String,String> reWriter) {
            if( target==null ) throw new IllegalArgumentException("target==null");
            this.target = target;

            if( reWriter==null ) throw new IllegalArgumentException("reWriter==null");
            this.reWriter = reWriter;

            lineCapture = new NewLine.LineCapture(line -> {
                var line2 = reWriter.apply(line);
                try {
                    target.write(line2);
                } catch (IOException e) {
                    throw new IOError(e);
                }
            }, crlf -> {
                try {
                    target.write("\n");
                } catch (IOException e) {
                    throw new IOError(e);
                }
            });
        }

        protected Function<String,String> reWriter(){ return reWriter; }

        @SuppressWarnings("unchecked")
        protected SELF reWriter(Function<String,String> reWriter){
            if( reWriter==null ) throw new IllegalArgumentException("reWriter==null");
            this.reWriter = reWriter;
            return (SELF) this;
        }

        @SuppressWarnings("unchecked")
        protected SELF linePrefix(String prefix){
            if( prefix==null ) throw new IllegalArgumentException("prefix==null");
            this.reWriter = line -> prefix + line;
            return (SELF) this;
        }

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            lineCapture.write(cbuf,off,len);
        }

        @Override
        public void flush() {
            lineCapture.flush();
        }

        @Override
        public void close() throws IOException {
            target.close();
        }

        public void print(String message){
            if( message==null ) throw new IllegalArgumentException("message==null");
            try {
                write(message);
            } catch (IOException e) {
                throw new IOError(e);
            }
        }

        public void println(String message){
            if( message==null ) throw new IllegalArgumentException("message==null");
            try {
                write(message);
                write("\n");
            } catch (IOException e) {
                throw new IOError(e);
            }
        }
    }

    public static class LineReWriter extends BaseLineReWriter<LineReWriter> {
        public LineReWriter(Writer target, Function<String, String> reWriter) {
            super(target, reWriter);
        }

        @Override
        public Function<String, String> reWriter() {
            return super.reWriter();
        }

        @Override
        public LineReWriter reWriter(Function<String, String> reWriter) {
            return super.reWriter(reWriter);
        }

        @Override
        public LineReWriter linePrefix(String prefix) {
            return super.linePrefix(prefix);
        }
    }

    public static class IndentWriter extends BaseLineReWriter<LineReWriter> {
        public IndentWriter(Writer target) {
            super(target, s -> s);
            //noinspection resource
            reWriter(this::rewriteLine);
        }

        private String rewriteLine(String s){
            return (level>0 ? tab.repeat(level) : "") + s;
        }

        private int level = 0;
        private String tab = "  ";

        public int level(){ return level; }
        public IndentWriter level(int lvl){
            if( lvl<0 ) throw new IllegalArgumentException("lvl<0");
            this.level = lvl;
            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        public IndentWriter incLevel(){
            return level(level()+1);
        }

        public IndentWriter decLevel(){
            return level>0 ? level(level-1) : this;
        }

        public IndentWriter indent( Runnable code ){
            if( code==null ) throw new IllegalArgumentException("code==null");
            //noinspection resource
            incLevel();
            code.run();
            return decLevel();
        }

        public IndentWriter indent( Consumer<? super Writer> code ){
            if( code==null ) throw new IllegalArgumentException("code==null");
            //noinspection resource
            incLevel();
            code.accept(this);
            return decLevel();
        }
    }
}
