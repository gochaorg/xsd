package xyz.cofe.xsd.ui.files;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.ajax.XMLHttpRequest;
import org.teavm.jso.core.JSMap;
import org.teavm.jso.core.JSString;
import org.teavm.jso.json.JSON;
import xyz.cofe.im.struct.ImList;
import xyz.cofe.xsd.ui.json.ArrayAdapter;
import xyz.cofe.xsd.ui.json.JSAdapter;
import xyz.cofe.xsd.ui.json.ObjectAdapter;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class FilesClient {
    public sealed interface PathObj {
        public record Directory(String name, String path) implements PathObj {}
        public record File(String name, String path) implements PathObj {}
    }

    @JSBody(params = {"map", "key"}, script = "return map[key];")
    private static native JSObject getValue(JSMap map, JSString key);

    @JSBody(params = {"obj"}, script = "console.log(obj);")
    private static native void log(Object obj);

    @JSBody(params = {"obj"}, script = "console.log(typeof(obj));")
    private static native void logTypeof(Object obj);

    @JSBody(params = {"obj"}, script = "return typeof(obj);")
    private static native String typeof(Object obj);

    @JSBody(params = {"obj"}, script = "return Array.isArray(obj);")
    private static native boolean isArray(Object obj);

    public ImList<PathObj> listFiles(String directory){
        var complete = new AtomicReference<String>(null);

        var xhr = XMLHttpRequest.create();

        xhr.onComplete(() -> {
            complete.set(xhr.getResponseText());
        });
        xhr.onError(err->{
            System.out.println("xhr.onError");
        });

        xhr.open("GET", directory, false);
        xhr.setRequestHeader("Accept", "application/json");
        xhr.send();

        var jsonString = complete.get();
        if( jsonString==null )return ImList.empty();

        var res = new ArrayList<PathObj>();
        var json = JSON.parse(jsonString);

        JSAdapter.of(json).each( jsn -> {
            if( jsn instanceof ArrayAdapter arr ){
                for( var itm : arr ){
                    if( itm instanceof ObjectAdapter obj ) {
                        obj.getString("name").flatMap( name -> obj.getBoolean("dir").map( dir -> {
                            if( dir ){
                                res.add(new PathObj.Directory(name, directory));
                            }else{
                                res.add(new PathObj.File(name, directory));
                            }
                            return null;
                        }));
                    }
                }
            }
        });

        return ImList.from(res);
    }
}
