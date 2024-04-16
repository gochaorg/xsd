package xyz.cofe.xsd.om;

import org.junit.jupiter.api.Test;
import xyz.cofe.im.struct.Result;
import xyz.cofe.nixpath.CanonAbsPath;
import xyz.cofe.nixpath.UnixPath;

import java.net.URI;

import static xyz.cofe.xsd.om.XMLParseTest.xmlDocResource;

public class XSDLoaderTest {
    private String base = "/XMLSchemas";
    private String xsd1 = "/XMLSchemas/ed/cbr_ed101_v2024.4.0.xsd";

    @Test
    public void tryLoad(){
        var xsdLdr = new XsdLoader(
            uri -> {
                String path = uri.getPath();
                if( path.matches("/?ed/cbr_ed101_v2024.4.0.xsd")){
                    var xdoc = xmlDocResource("/XMLSchemas/ed/cbr_ed101_v2024.4.0.xsd");
                    var xsd = new XsdDoc(xdoc);
                    return new XsdLoader.LoadResult.Success<>(xsd);
                }

                var pathRes = UnixPath.parse(path);
                if(pathRes.isOk()){
                    var nixPath = pathRes.toOptional().get();
                    if( nixPath.isRelative() ){
                        CanonAbsPath baseNixPath = CanonAbsPath.parse(base).toOptional().get();
                        Result<CanonAbsPath, String> targetNixPath = baseNixPath.resolve(nixPath);

                        XsdLoader.LoadResult<XsdDoc> res = targetNixPath.fold( target -> {
                            var xdoc1 = xmlDocResource(target.toString());
                            var xsd1 = new XsdDoc(xdoc1);
                            return new XsdLoader.LoadResult.Success<XsdDoc>(xsd1);
                        }, ignore -> new XsdLoader.LoadResult.Fail<XsdDoc>(""));

                        return res;
                    }else{
                        var resName = base+nixPath.toString();
                        var xdoc1 = xmlDocResource(resName);
                        var xsd1 = new XsdDoc(xdoc1);
                        return new XsdLoader.LoadResult.Success<XsdDoc>(xsd1);
                    }
                }

                return new XsdLoader.LoadResult.Fail<>("");
            },
            (base,ref) -> {
                String refPath = ref.getPath();
                return base.resolve(refPath);
            }
        );

        xsdLdr.setLoadLog( ((uri, xsdDocLoadResult) -> {
            System.out.println(
                "load "+uri+" "+(xsdDocLoadResult instanceof XsdLoader.LoadResult.Success<XsdDoc> succ
                    ? "success "+succ.value().getTargetNamespace()
                    : "fail"));
        }));
        var res = xsdLdr.load(URI.create("/ed/cbr_ed101_v2024.4.0.xsd"));

    }
}
