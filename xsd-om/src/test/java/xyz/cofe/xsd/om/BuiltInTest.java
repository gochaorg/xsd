package xyz.cofe.xsd.om;

import org.junit.jupiter.api.Test;
import xyz.cofe.coll.im.Tuple2;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BuiltInTest {
    @Test
    public void NCName1(){
        Optional<Tuple2<BuiltInTypes.NCNAME, Integer>> res = BuiltInTypes.NCNAME.parse("ed:ED101", 0);
        assertTrue(res.isPresent());
        assertTrue(res.map(a -> a._1().value().equals("ed")).orElse(false));
    }

    @Test
    public void QName1(){
        var res = BuiltInTypes.QName.parse("ed:ED101");
        assertTrue(res.isOk());

        var r = res.fold(
            qName -> qName.localPart().equals("ED101") && qName.prefix().get().equals("ed"),
            ignore -> false
        );

        assertTrue(r);
    }
}
