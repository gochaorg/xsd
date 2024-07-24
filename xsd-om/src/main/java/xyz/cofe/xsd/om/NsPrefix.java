package xyz.cofe.xsd.om;

import xyz.cofe.coll.im.ImList;
import xyz.cofe.coll.im.Result;
import xyz.cofe.xml.XmlAttr;

@Deprecated
public final class NsPrefix {
    public static Result<NsPrefix,String> parse(XmlAttr attr){
        if( attr==null ) throw new IllegalArgumentException("attr==null");
        if( attr.getPrefix()!=null && attr.getPrefix().equals("xmlns") ){
            return Result.ok(new NsPrefix(attr));
        }
        return Result.error("expect xmlns prefix at attribute");
    }

    public static ImList<NsPrefix> parseList(XmlAttr attr){
        if( attr==null ) throw new IllegalArgumentException("attr==null");
        return parse(attr).fold(ImList::of, err -> ImList.of());
    }

    public final XmlAttr xmlAttr;

    public NsPrefix(XmlAttr xmlAttr) {
        if( xmlAttr==null ) throw new IllegalArgumentException("xmlAttr==null");
        this.xmlAttr = xmlAttr;
    }

    public String getPrefix(){
        return xmlAttr.getLocalName();
    }

    public String getNamespace(){
        return xmlAttr.getValue();
    }
}
