package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.im.struct.Tuple2;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class XsdLoader {
    public sealed interface LoadResult<R> {
        record Success<R>( R value ) implements LoadResult<R> {}
        record Fail<R>( String message ) implements LoadResult<R> {}
    }

    private final Function<URI, LoadResult<XsdDoc>> partialLoader;
    private final BiFunction<URI, URI, URI> resolveBaseTarget;

    public XsdLoader(Function<URI, LoadResult<XsdDoc>> partialLoader, BiFunction<URI, URI, URI> resolveBaseTarget) {
        if( partialLoader ==null ) throw new IllegalArgumentException("baseDocLoader==null");
        if( resolveBaseTarget==null ) throw new IllegalArgumentException("resolveBaseTarget==null");
        this.partialLoader = partialLoader;
        this.resolveBaseTarget = resolveBaseTarget;
    }

    private final Map<URI, LoadResult<XsdDoc>> loaded = new HashMap<>();

    //region loadLog : BiConsumer<URI, LoadResult<XsdDoc>>
    private BiConsumer<URI, LoadResult<XsdDoc>> loadLog = null;

    public BiConsumer<URI, LoadResult<XsdDoc>> getLoadLog() {
        return loadLog;
    }

    public void setLoadLog(BiConsumer<URI, LoadResult<XsdDoc>> loadLog) {
        this.loadLog = loadLog;
    }
    //endregion

    public LoadResult<XsdDoc> load(URI uri){
        if( uri==null ) throw new IllegalArgumentException("uri==null");

        var cached = loaded.get(uri);
        if( cached!=null )return cached;

        var res = partialLoader.apply(uri);
        var logLoad = this.getLoadLog();
        if( logLoad!=null )logLoad.accept(uri, res);

        loaded.put(uri, res);

        if( res instanceof LoadResult.Success<XsdDoc> succ ){
            succ.value().getIncludes().forEach(xsdInclude -> {
                loadRef(xsdInclude.getSchemaRefs(), uri, xsdInclude.getXsdDocs());
            });
            succ.value().getImports().forEach(xsdInclude -> {
                loadRef(xsdInclude.getSchemaRefs(), uri, xsdInclude.getXsdDocs());
            });
        }

        return res;
    }

    private void loadRef(ImList<URI> xsdInclude, URI uri, Map<URI, LinkedDoc> xsdInclude1) {
        xsdInclude.map(ref -> Tuple2.of(ref, resolveBaseTarget.apply(uri, ref))).each(resolved -> {
            var targetUri = resolved.b();
            var srcUri = resolved.a();

            var res = load(targetUri);
            xsdInclude1.put(srcUri, new LinkedDoc(targetUri, res));
        });
    }
}
