package xyz.cofe.xsd.om;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static xyz.cofe.xsd.om.XMLParseTest.xmlDocResource;

public class XSDParseTest {
    @Test
    public void test1(){
        var res = "/XMLSchemas/ed/cbr_ed101_v2024.4.0.xsd";
        var xdoc = xmlDocResource(res);
        var xsd = new XsdDoc(xdoc);
    }
}
