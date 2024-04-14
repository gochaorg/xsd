package xyz.cofe.xsd.http.mount;

import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.server.Response;

public record Mime(String name) {
    public static final Mime textPlain = new Mime("text/plain");
    public static final Mime textHtml = new Mime("text/html");
    public static final Mime textJavascript = new Mime("text/javascript");
    public static final Mime textCss = new Mime("text/css");
    public static final Mime textXml = new Mime("text/xml");
    public static final Mime textXslt = new Mime("text/xsl");
    public static final Mime imageGif = new Mime("image/gif");
    public static final Mime imagePng = new Mime("image/png");
    public static final Mime imageJpeg = new Mime("image/jpeg");
    public static final Mime imageSvg = new Mime("image/svg+xml");
    public static final Mime applicationOctetStream = new Mime("application/octet-stream");

    public static Mime of(FileExtension extension){
        if( extension==null ) throw new IllegalArgumentException("extension==null");
        return switch (extension.name().toLowerCase()){
            case "txt", "java" -> textPlain;
            case "css" -> textCss;
            case "html" -> textHtml;
            case "js" -> textJavascript;
            case "xml", "xsd" -> textXml;
            case "xsl","xslt" -> textXslt;
            case "jpeg","jpg" -> imageJpeg;
            case "gif" -> imageGif;
            case "png" -> imagePng;
            case "svg" -> imageSvg;
            default -> applicationOctetStream;
        };
    }

    public void setContentType(Response response){
        if( response==null ) throw new IllegalArgumentException("response==null");
        response.getHeaders().put(HttpHeader.CONTENT_TYPE, name());
    }
}
