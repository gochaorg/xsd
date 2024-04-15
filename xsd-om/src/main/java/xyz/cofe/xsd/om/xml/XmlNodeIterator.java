package xyz.cofe.xsd.om.xml;

import xyz.cofe.xsd.om.coll.ImList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class XmlNodeIterator implements Iterator<ImList<XmlNode>> {
    public XmlNodeIterator(Iterable<ImList<XmlNode>> nodes){
        if( nodes==null ) throw new IllegalArgumentException("nodes==null");
        for( var n : nodes ){
            workSet.add(n);
        }
    }

    @SuppressWarnings("UseBulkOperation")
    public XmlNodeIterator(ImList<XmlNode> ... nodes){
        if( nodes==null ) throw new IllegalArgumentException("nodes==null");
        for( var n : nodes ){
            workSet.add(n);
        }
    }

    private final List<ImList<XmlNode>> workSet = new ArrayList<>();

    @Override
    public boolean hasNext() {
        return !workSet.isEmpty();
    }

    @Override
    public ImList<XmlNode> next() {
        var res = workSet.remove(0);
        res.head().ifPresent( h -> {
            if( h instanceof XmlElem e ){
                workSet.addAll(
                    0,
                    e.getChildren().stream().map(res::prepend).toList()
                );
            }
        });
        return res;
    }
}
