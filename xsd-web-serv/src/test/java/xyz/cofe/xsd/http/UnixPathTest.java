package xyz.cofe.xsd.http;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UnixPathTest {
    @Test
    public void normal(){
        var pathOpt = UnixPath.parse("/a/b");
        assertTrue(pathOpt.isPresent());

        var path = pathOpt.get();
        assertTrue(path.isAbsolute());

        assertTrue(path.getPathComponents().size()==2);
    }

    @Test
    public void outside(){
        var pathOpt = UnixPath.parse("/../a/b");
        assertTrue(pathOpt.isPresent());

        var path = pathOpt.get();
        assertTrue(path.isEscapeOfRoot());
    }
}
