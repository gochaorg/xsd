package xyz.cofe.xsd.om;

import org.junit.jupiter.api.Test;
import xyz.cofe.coll.im.ImList;
import xyz.cofe.coll.im.Result;
import xyz.cofe.xsd.cmpl.XsdCompile;
import xyz.cofe.xsd.om.BuiltInTypes.NCNAME;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static xyz.cofe.xsd.text.Indent.indent;

public class XSDParseTest {
    @Test
    public void namespace() {
        var xsd = new XSDLoaderTest().verbose(false).syncLoad();
        xsd.getNamespaces().getNamespaceMap().forEach((name, ns) -> {
            System.out.println("name " + name);
        });

        var el = xsd.getElements().head().get();
        var refType = el.getRefType().toOptional().get();

        System.out.println(refType);

        XsdComplexType ct = (XsdComplexType) refType;
        var extendRes = ct.getExtensionTypeDef();
        System.out.println(extendRes);
    }

    @Test
    public void compile() {
        var xsd = new XSDLoaderTest().verbose(false).syncLoad();
        xsd.getNamespaces().getNamespaceMap().forEach((name, ns) -> {
        });

        XsdElement el = xsd.getElements().head().get();

        XsdCompile compile = new XsdCompile();

        var out = new PrintWriter(System.out);
        out.println(compile.compile(el));
        out.flush();
    }
}
