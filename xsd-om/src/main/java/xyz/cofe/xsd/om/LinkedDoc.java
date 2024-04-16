package xyz.cofe.xsd.om;

import java.net.URI;

public record LinkedDoc(URI uri, XsdLoader.LoadResult<XsdDoc> doc) {}
