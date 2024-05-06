package xyz.cofe.xsd.ui.json;

import org.teavm.jso.JSBody;

public class JS {
    @JSBody(params = {"obj"}, script = "console.log(obj);")
    public static native void log(Object obj);

    @JSBody(params = {"obj"}, script = "return typeof(obj);")
    public static native String typeof(Object obj);
}
