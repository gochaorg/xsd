package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;

public class Namespace {
    public final String name;
    public final ImList<XsdSchema> xsdDocs;

    public Namespace(String name, ImList<XsdSchema> xsdDocs){
        if( name==null ) throw new IllegalArgumentException("name==null");
        if( xsdDocs==null ) throw new IllegalArgumentException("xsdDocs==null");
        this.xsdDocs = xsdDocs;
        this.name = name;
    }
}
