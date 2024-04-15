package xyz.cofe.xsd.om;

import xyz.cofe.xsd.om.XsdDoc;

import java.util.function.Consumer;

public interface XsdLoader {
    public void load(String schemaLocation, Consumer<XsdDoc> xsdDoc);
}
