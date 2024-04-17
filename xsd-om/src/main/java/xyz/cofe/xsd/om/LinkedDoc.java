package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.Result;

import java.net.URI;

public record LinkedDoc(URI uri, Result<XsdSchema,String> doc) {}
