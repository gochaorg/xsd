package xyz.cofe.nixpath;

import org.junit.jupiter.api.Test;
import xyz.cofe.nixpath.UnixPath;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UnixPathTest {
    @Test
    public void normal(){
        var pathOpt = UnixPath.parse("/a/b");
        assertTrue(pathOpt.isOk());

        var path = pathOpt.fold( a -> a, b -> { throw new RuntimeException("!");});
        assertTrue(path.isAbsolute());

        assertTrue(path.getPathComponents().size()==2);
    }

    @Test
    public void outside(){
        var pathOpt = UnixPath.parse("/../a/b");
        assertTrue(pathOpt.isOk());

        var path = pathOpt.fold( a -> a, b -> { throw new RuntimeException("!");});
        assertTrue(path.isEscapeOfRoot());
    }

    @Test
    public void resolve1(){
        var baseRes = CanonAbsPath.parse("/base");
        var base = baseRes.fold(a -> a, b -> { throw new RuntimeException("!"); });

        var rel1Res = base.resolve("rel");
        System.out.println(rel1Res);
        assertTrue(rel1Res.isOk());
        assertTrue(rel1Res.map(CanonAbsPath::toString).map("/base/rel"::equals).toOptional().orElse(false) );

        var rel2Res = base.resolve("../rel");
        System.out.println(rel2Res);
        assertTrue(rel2Res.isOk());
        assertTrue(rel2Res.map(CanonAbsPath::toString).map("/rel"::equals).toOptional().orElse(false) );

        var rel3Res = base.resolve("../../rel");
        System.out.println(rel3Res);
        assertTrue(rel3Res.isError());

        var rel4Res = base.resolve("/abs");
        System.out.println(rel4Res);
        assertTrue(rel4Res.isOk());
        assertTrue(rel4Res.map(CanonAbsPath::toString).map("/abs"::equals).toOptional().orElse(false) );

        var rel5Res = base.resolve("rel/sub");
        System.out.println(rel5Res);
        assertTrue(rel5Res.isOk());
        assertTrue(rel5Res.map(CanonAbsPath::toString).map("/base/rel/sub"::equals).toOptional().orElse(false) );

        var rel6Res = base.resolve("rel/sub/");
        System.out.println(rel6Res);
        assertTrue(rel6Res.isOk());
        assertTrue(rel6Res.map(CanonAbsPath::toString).map("/base/rel/sub/"::equals).toOptional().orElse(false) );
    }

    @Test
    public void resolve3(){
        var baseRes = CanonAbsPath.parse("/base/");
        var base = baseRes.fold(a -> a, b -> { throw new RuntimeException("!"); });

        var rel1Res = base.resolve("rel");
        System.out.println(rel1Res);
        assertTrue(rel1Res.isOk());
        assertTrue(rel1Res.map(CanonAbsPath::toString).map("/base/rel"::equals).toOptional().orElse(false) );

        var rel2Res = base.resolve("../rel");
        System.out.println(rel2Res);
        assertTrue(rel2Res.isOk());
        assertTrue(rel2Res.map(CanonAbsPath::toString).map("/rel"::equals).toOptional().orElse(false) );

        var rel3Res = base.resolve("../../rel");
        System.out.println(rel3Res);
        assertTrue(rel3Res.isError());

        var rel4Res = base.resolve("/abs");
        System.out.println(rel4Res);
        assertTrue(rel4Res.isOk());
        assertTrue(rel4Res.map(CanonAbsPath::toString).map("/abs"::equals).toOptional().orElse(false) );

        var rel5Res = base.resolve("rel/sub");
        System.out.println(rel5Res);
        assertTrue(rel5Res.isOk());
        assertTrue(rel5Res.map(CanonAbsPath::toString).map("/base/rel/sub"::equals).toOptional().orElse(false) );

        var rel6Res = base.resolve("rel/sub/");
        System.out.println(rel6Res);
        assertTrue(rel6Res.isOk());
        assertTrue(rel6Res.map(CanonAbsPath::toString).map("/base/rel/sub/"::equals).toOptional().orElse(false) );
    }

    @Test
    public void resolve2(){
        var baseRes = CanonAbsPath.parse("/");
        var base = baseRes.fold(a -> a, b -> { throw new RuntimeException("!"); });

        var rel1Res = base.resolve("rel");
        System.out.println(rel1Res);
        assertTrue(rel1Res.isOk());
        assertTrue(rel1Res.map(CanonAbsPath::toString).map("/rel"::equals).toOptional().orElse(false) );

        var rel2Res = base.resolve("../rel");
        System.out.println(rel2Res);
        assertTrue(rel2Res.isError());
//        assertTrue(rel2Res.isOk());
//        assertTrue(rel2Res.map(CanonAbsPath::toString).map("/rel"::equals).toOptional().orElse(false) );

        var rel3Res = base.resolve("../../rel");
        System.out.println(rel3Res);
        assertTrue(rel3Res.isError());

        var rel4Res = base.resolve("/abs");
        System.out.println(rel4Res);
        assertTrue(rel4Res.isOk());
        assertTrue(rel4Res.map(CanonAbsPath::toString).map("/abs"::equals).toOptional().orElse(false) );

        var rel5Res = base.resolve("rel/sub");
        System.out.println(rel5Res);
        assertTrue(rel5Res.isOk());
        assertTrue(rel5Res.map(CanonAbsPath::toString).map("/rel/sub"::equals).toOptional().orElse(false) );

        var rel6Res = base.resolve("rel/sub/");
        System.out.println(rel6Res);
        assertTrue(rel6Res.isOk());
        assertTrue(rel6Res.map(CanonAbsPath::toString).map("/rel/sub/"::equals).toOptional().orElse(false) );
    }
}
