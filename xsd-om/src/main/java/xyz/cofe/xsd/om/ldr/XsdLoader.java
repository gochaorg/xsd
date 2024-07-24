package xyz.cofe.xsd.om.ldr;

import xyz.cofe.coll.im.ImList;
import xyz.cofe.coll.im.Result;
import xyz.cofe.coll.im.Tuple2;
import xyz.cofe.xsd.om.LinkedDoc;
import xyz.cofe.xsd.om.XsdSchema;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class XsdLoader {

    private final Function<URI, Result<XsdSchema,String>> partialLoader;
    private final BiConsumer<URI, Consumer<Result<XsdSchema,String>>> asyncPartialLoader = null;
    private final BiFunction<URI, URI, URI> resolveBaseTarget;

    public XsdLoader(Function<URI, Result<XsdSchema,String>> partialLoader, BiFunction<URI, URI, URI> resolveBaseTarget) {
        if (partialLoader == null) throw new IllegalArgumentException("baseDocLoader==null");
        if (resolveBaseTarget == null) throw new IllegalArgumentException("resolveBaseTarget==null");
        this.partialLoader = partialLoader;
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

    public Result<XsdSchema,String> load(URI uri) {
        if (uri == null) throw new IllegalArgumentException("uri==null");

        var cached = loaded.get(uri);
        if (cached != null) return cached;

        var res = partialLoader.apply(uri);
        loaded.put(uri, res);

        var logLoad = this.getLoadLog();
        if (logLoad != null) logLoad.accept(uri, res);

        if (res.getOk().isPresent()) {
            res.getOk().get().getIncludes().forEach(xsdInclude -> {
                loadRef(xsdInclude.getSchemaRefs(), uri, xsdInclude.getXsdDocs());
            });
            res.getOk().get().getImports().forEach(xsdInclude -> {
                loadRef(xsdInclude.getSchemaRefs(), uri, xsdInclude.getXsdDocs());
            });
        }

        return res;
    }

    private void loadRef(ImList<URI> xsdInclude, URI uri, Map<URI, LinkedDoc> xsdInclude1) {
        xsdInclude.map(ref -> Tuple2.of(ref, resolveBaseTarget.apply(uri, ref))).each(resolved -> {
            var targetUri = resolved._2();
            var srcUri = resolved._1();

            var res = load(targetUri);
            xsdInclude1.put(srcUri, new LinkedDoc(targetUri, res));
        });
    }

    private void load(URI uri, Consumer<Result<XsdSchema,String>> resultConsumer) {
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

            if (res.getOk().isPresent()) {
                AtomicInteger cntTask = new AtomicInteger(0);

                Runnable checkNotify = () -> {
                    if (cntTask.get() == 0) {
                        resultConsumer.accept(res);
                    }
                };

                res.getOk().get().getIncludes().forEach(xsdInclude -> {
                    xsdInclude.getSchemaRefs()
                        .map(ref -> Tuple2.of(ref, resolveBaseTarget.apply(uri, ref)))
                        .each(resolvUriTup -> {
                            var targetUri = resolvUriTup._2();
                            var srcUri = resolvUriTup._1();

                            cntTask.incrementAndGet();
                            load(targetUri, refDocLoadRes -> {
                                xsdInclude.getXsdDocs().put(srcUri, new LinkedDoc(targetUri, refDocLoadRes));
                                cntTask.decrementAndGet();
                                checkNotify.run();
                            });
                        });
                });

                checkNotify.run();
            } else {
                resultConsumer.accept(res);
            }
        });
    }
}
