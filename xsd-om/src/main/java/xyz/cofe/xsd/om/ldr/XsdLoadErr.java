package xyz.cofe.xsd.om.ldr;

import xyz.cofe.im.struct.Result;
import xyz.cofe.xsd.om.XsdDoc;

public interface XsdLoadErr {
    public record XsdPartialLoadErr<E>(Result.Err<XsdDoc,E> error) {}

}
