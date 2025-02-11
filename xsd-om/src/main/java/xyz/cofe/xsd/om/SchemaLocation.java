package xyz.cofe.xsd.om;

import xyz.cofe.coll.im.ImList;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

public interface SchemaLocation {

    public Map<URI, LinkedDoc> getXsdDocs();

    Optional<String> getSchemaLocation();
    default ImList<URI> getSchemaRefs(){
        var refs = ImList.<URI>of();
        var slocOpt = getSchemaLocation();
        if(slocOpt.isPresent()){
            var locations = slocOpt.get().split("\\s+");
            for( var location : locations ){
                try {
                    var uri = URI.create(location);
                    refs = refs.prepend(uri);
                } catch (IllegalArgumentException ex){
                    System.err.println("can't parse "+location+" "+ex);
                }
            }
            return refs.reverse();
        }else{
            return refs;
        }
    }
}
