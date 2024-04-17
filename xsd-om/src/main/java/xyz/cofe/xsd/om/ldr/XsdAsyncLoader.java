package xyz.cofe.xsd.om.ldr;

import xyz.cofe.im.struct.Result;
import xyz.cofe.im.struct.Tuple2;
import xyz.cofe.xsd.om.LinkedDoc;
import xyz.cofe.xsd.om.XsdSchema;
import xyz.cofe.xsd.om.XsdSchemaLocation;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class XsdAsyncLoader {

    private final BiConsumer<URI, Consumer<Result<XsdSchema,String>>> asyncPartialLoader;
    private final BiFunction<URI, URI, URI> resolveBaseTarget;

    public XsdAsyncLoader(BiConsumer<URI, Consumer<Result<XsdSchema,String>>> asyncPartialLoader, BiFunction<URI, URI, URI> resolveBaseTarget) {
        if (asyncPartialLoader == null) throw new IllegalArgumentException("asyncPartialLoader==null");
        if (resolveBaseTarget == null) throw new IllegalArgumentException("resolveBaseTarget==null");
        this.asyncPartialLoader = asyncPartialLoader;
        this.resolveBaseTarget = resolveBaseTarget;
    }

    private final Map<URI, Result<XsdSchema,String>> loaded = new HashMap<>();

    //region loadLog : BiConsumer<URI, Result<XsdDoc,String>>
    private BiConsumer<URI, Result<XsdSchema,String>> loadLog = null;

    public BiConsumer<URI, Result<XsdSchema,String>> getLoadLog() {
        return loadLog;
    }

    public void setLoadLog(BiConsumer<URI, Result<XsdSchema,String>> loadLog) {
        this.loadLog = loadLog;
    }
    //endregion

    public void load(URI uri, Consumer<Result<XsdSchema,String>> resultConsumer) {
        if (uri == null) throw new IllegalArgumentException("uri==null");
        if (resultConsumer == null) throw new IllegalArgumentException("resultConsumer==null");

        var cached = loaded.get(uri);
        if (cached != null) {
            resultConsumer.accept(cached);
            return;
        }

        asyncPartialLoader.accept(uri, res -> {
            loaded.put(uri, res);

            var logLoad = this.getLoadLog();
            if (logLoad != null) logLoad.accept(uri, res);

            if (res instanceof Result.Ok<XsdSchema, String> succ) {
                AtomicInteger cntTask = new AtomicInteger(0);

                Runnable checkNotify = () -> {
                    if (cntTask.get() == 0) {
                        resultConsumer.accept(res);
                    }
                };

                Consumer<XsdSchemaLocation> xsdLocal = lcl -> {
                    lcl.getSchemaRefs()
                        .map(ref -> Tuple2.of(ref, resolveBaseTarget.apply(uri, ref)))
                        .each(resolvUriTup -> {
                            var targetUri = resolvUriTup.b();
                            var srcUri = resolvUriTup.a();

                            cntTask.incrementAndGet();
                            load(targetUri, refDocLoadRes -> {
                                lcl.getXsdDocs().put(srcUri, new LinkedDoc(targetUri, refDocLoadRes));
                                cntTask.decrementAndGet();
                                checkNotify.run();
                            });
                        });
                };

                succ.value().getIncludes().forEach(xsdLocal);
                succ.value().getImports().forEach(xsdLocal);

                checkNotify.run();
            } else {
                resultConsumer.accept(res);
            }
        });
    }
}
