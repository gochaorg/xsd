package xyz.cofe.xsd.om;

import org.junit.jupiter.api.Test;
import xyz.cofe.xsd.om.xml.XmlDoc;
import xyz.cofe.xsd.om.xml.jt.XMLDocAdapter;
import xyz.cofe.xsd.om.xml.print.XmlPrinter;

import java.io.IOException;
import java.nio.charset.Charset;

public class XMLParseTest {
    @SuppressWarnings("DataFlowIssue")
    public static String xmlTextResource(String resourceName, Charset cs){
        if( resourceName==null ) throw new IllegalArgumentException("resourceName==null");
        if( cs==null ) throw new IllegalArgumentException("cs==null");

        var resUrl = XMLParseTest.class.getResource(resourceName);
        try( var strm = resUrl.openStream() ){
            return new String(strm.readAllBytes(), cs);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String xmlTextResource(String resourceName){
        return xmlTextResource(resourceName,Charset.forName("cp1251"));
    }

    public static XmlDoc xmlDocResource(String resourceName){
        return XMLDocAdapter.parse(xmlTextResource(resourceName));
    }

    @Test
    public void test(){
        XmlDoc xdoc = XMLDocAdapter.parse(xmlTextResource("/XMLSchemas/xml.xsd"));
        StringBuilder buff = new StringBuilder();
        new XmlPrinter(buff).print(xdoc);
        System.out.println(buff);
    }
}
