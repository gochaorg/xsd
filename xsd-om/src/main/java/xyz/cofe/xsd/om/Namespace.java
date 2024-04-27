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

    private ImList<XsdElement> elements;
    public ImList<XsdElement> getElements() {
        if( elements!=null )return elements;
        elements = xsdDocs.flatMap(XsdSchema::getElements);
        return elements;
    }

    private ImList<XsdComplexType> complexTypes;
    public ImList<XsdComplexType> getComplexTypes(){
        if( complexTypes!=null )return complexTypes;
        complexTypes = xsdDocs.flatMap(XsdSchema::getComplexTypes);
        return complexTypes;
    }

    private ImList<XsdSimpleType> simpleType;
    public ImList<XsdSimpleType> getSimpleType(){
        if( simpleType!=null )return simpleType;
        simpleType = xsdDocs.flatMap(XsdSchema::getSimpleTypes);
        return simpleType;
    }

    private ImList<TypeDef> typeDefs;
    public ImList<TypeDef> getTypeDefs(){
        if( typeDefs!=null )return typeDefs;
        ImList<TypeDef> ct = getComplexTypes().map(a -> a);
        ImList<TypeDef> st = getSimpleType().map(a -> a);
        typeDefs = ct.join(st);
        return typeDefs;
    }
}
