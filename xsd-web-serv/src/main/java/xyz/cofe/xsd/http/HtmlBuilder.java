package xyz.cofe.xsd.http;

import java.io.IOException;
import java.util.function.Consumer;

public class HtmlBuilder {
    private final Appendable out;
    public HtmlBuilder(Appendable out){
        if( out==null ) throw new IllegalArgumentException("out==null");
        this.out = out;
    }

    private static String escapeHTML(String s) {
        StringBuilder out = new StringBuilder(Math.max(16, s.length()));
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c > 127 || c == '"' || c == '\'' || c == '<' || c == '>' || c == '&') {
                out.append("&#");
                out.append((int) c);
                out.append(';');
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }

    public HtmlBuilder text(String text){
        if( text==null ) throw new IllegalArgumentException("text==null");
        try {
            out.append(escapeHTML(text));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public class Tag {
        private final String name;

        public Tag(String name) {
            if( name==null ) throw new IllegalArgumentException("name==null");
            this.name = name;
            try {
                out.append("<").append(name);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public Tag attribute(String name, String value) {
            if( name==null ) throw new IllegalArgumentException("name==null");
            if( value==null ) throw new IllegalArgumentException("value==null");

            try {
                out.append(" ").append(name).append("=").append("\"").append(
                    value
                        .replace("&","&amp;")
                        .replace("<","&lt;")
                        .replace(">","&gt;")
                        .replace("'","&apos;")
                        .replace("\"","&quot;")
                ).append("\"");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return this;
        }

        public HtmlBuilder body(Consumer<HtmlBuilder> body){
            if( body==null ) throw new IllegalArgumentException("body==null");
            try {
                out.append(">");
                body.accept(HtmlBuilder.this);
                out.append("</").append(name).append(">");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return HtmlBuilder.this;
        }

        public HtmlBuilder body(String text){
            if( text==null ) throw new IllegalArgumentException("text==null");
            try {
                out.append(">");
                HtmlBuilder.this.text(text);
                out.append("</").append(name).append(">");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return HtmlBuilder.this;
        }

        public HtmlBuilder body(){
            try {
                out.append("/>");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return HtmlBuilder.this;
        }
    }

    public Tag tag(String name){
        if( name==null ) throw new IllegalArgumentException("name==null");
        return new Tag(name);
    }
}
