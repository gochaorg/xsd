package xyz.cofe.xsd.om;

import org.junit.jupiter.api.Test;
import xyz.cofe.im.struct.Result;
import xyz.cofe.nixpath.CanonAbsPath;
import xyz.cofe.nixpath.UnixPath;
import xyz.cofe.xsd.om.ldr.XsdAsyncLoader;
import xyz.cofe.xsd.om.ldr.XsdLoader;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static xyz.cofe.xsd.om.XMLParseTest.xmlDocResource;

public class XSDLoaderTest {
    private String base = "/XMLSchemas";
    private String testUri = "/ed/cbr_ed101_v2024.4.0.xsd";

    private Result<XsdDoc, String> load(URI uri) {
        var pathRes = UnixPath.parse(uri.getPath());
        if (pathRes.isOk()) {
            var nixPath = pathRes.toOptional().get();
            if (nixPath.isRelative()) {
                CanonAbsPath baseNixPath = CanonAbsPath.parse(base).toOptional().get();
                Result<CanonAbsPath, String> targetNixPath = baseNixPath.resolve(nixPath);

                return targetNixPath.fold(
                    target -> {
                        var xdoc1 = xmlDocResource(target.toString());
                        var xsd11 = new XsdDoc(xdoc1);
                        return Result.ok(xsd11);
                    },
                    ignore -> Result.err("")
                );
            } else {
                var resName = base + nixPath.toString();
                var xdoc1 = xmlDocResource(resName);
                var xsd1 = new XsdDoc(xdoc1);
                return Result.ok(xsd1);
            }
        }

        return Result.err("");
    }

    private URI resolveUri(URI base, URI ref) {
        String refPath = ref.getPath();
        return base.resolve(refPath);
    }

    @Test
    public void load_1() {
        var xsdLdr = new XsdLoader(
            this::load,
            this::resolveUri
        );

        xsdLdr.setLoadLog(((uri, xsdDocLoadResult) -> {
            System.out.println(
                "load " + uri + " " + (xsdDocLoadResult instanceof Result.Ok<XsdDoc, ?> succ
                    ? "success " + succ.value().getTargetNamespace()
                    : "fail"));
        }));
        var res = xsdLdr.load(URI.create(testUri));
    }

    @Test
    public void asyncLoad(){
        var xsdLdr = new XsdAsyncLoader(
            (uri,consumer) -> {
                consumer.accept(load(uri));
            },
            this::resolveUri
        );

        xsdLdr.setLoadLog(((uri, xsdDocLoadResult) -> {
            System.out.println(
                "load " + uri + " " + (xsdDocLoadResult instanceof Result.Ok<XsdDoc, ?> succ
                    ? "success " + succ.value().getTargetNamespace()
                    : "fail"));
        }));

        xsdLdr.load(URI.create(testUri), res -> {
            assertTrue(res.isOk());
        });
    }
}
