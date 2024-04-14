package xyz.cofe.xsd.http;

import org.junit.jupiter.api.Test;
import xyz.cofe.xsd.http.mount.MountUrl;
import xyz.cofe.xsd.http.mount.UnixPath;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MountUrlTest {
    @Test
    public void test(){
        var mnt = new MountUrl("/file");
        assertTrue( mnt.test("/abc").isEmpty() );
        assertTrue( mnt.test("/file/ab").map(n -> n.toString().equals("/ab")).orElse(false) );
        assertTrue( mnt.test("/file123/ab").isEmpty() );
    }

    @Test
    public void empty(){
        var mnt = new MountUrl("/file");
        assertTrue( mnt.test("/file").map(UnixPath::isEmpty).orElse(false) );
    }
}
