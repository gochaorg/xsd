package xyz.cofe.xsd.om;

import xyz.cofe.coll.im.ImList;
import xyz.cofe.coll.im.Result;
import xyz.cofe.coll.im.Tuple2;

import java.util.HashMap;
import java.util.Map;

public class Namespaces {
    private final ImList<XsdSchema> xsdDocs;

    public Namespaces(Iterable<XsdSchema> xsdDocs) {
        if (xsdDocs == null) throw new IllegalArgumentException("xsdDocs==null");
        this.xsdDocs = ImList.from(xsdDocs);
    }

    private Map<String, Namespace> namespaceMap;

    public Map<String, Namespace> getNamespaceMap() {
        if (namespaceMap != null) return namespaceMap;

        Map<String, ImList<XsdSchema>> m = new HashMap<>();
        for( var xsd : xsdDocs ){
            xsd.getTargetNamespace().ifPresent( name -> {
                m.put(
                    name,
                    m.getOrDefault(name, ImList.of()).prepend(xsd)
                );
            });
        }

        namespaceMap =
            ImList.from(m.entrySet()).toMap(
                e -> Tuple2.of(e.getKey(), new Namespace(e.getKey(), e.getValue()))
            );
        return namespaceMap;
    }

    public Result<Namespace, String> find(String name) {
        if( name==null ) throw new IllegalArgumentException("name==null");
        var ns = getNamespaceMap().get(name);
        if( ns==null )return Result.error("not found");
        return Result.ok(ns);
    }
}
