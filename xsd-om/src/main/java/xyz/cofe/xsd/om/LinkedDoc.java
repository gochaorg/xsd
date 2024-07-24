package xyz.cofe.xsd.om;

import xyz.cofe.coll.im.Result;

import java.net.URI;

public record LinkedDoc(URI uri, Result<XsdSchema,String> doc) {}
