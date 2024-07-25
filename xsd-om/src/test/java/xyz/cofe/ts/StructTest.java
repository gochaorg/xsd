package xyz.cofe.ts;

import org.junit.jupiter.api.Test;
import xyz.cofe.coll.im.ImList;
import xyz.cofe.coll.im.Result;
import xyz.cofe.xsd.ts.BuiltIn;

public class StructTest {
    @Test
    public void test(){
        var fld1 = new Field("a", Result.ok(BuiltIn.xsAnyURI));
        var fld2 = new Field("b", Result.ok(BuiltIn.xsAnyURI));
    }
}
