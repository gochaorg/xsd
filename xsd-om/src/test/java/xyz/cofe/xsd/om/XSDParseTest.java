package xyz.cofe.xsd.om;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static xyz.cofe.xsd.om.XMLParseTest.xmlDocResource;

public class XSDParseTest {
    @Test
    public void namespace(){
        var xsd = new XSDLoaderTest().verbose(false).syncLoad();
        xsd.getNamespaces().getNamespaceMap().forEach( (name,ns) -> {
            System.out.println("name "+name);
        });
    }
}
