package xyz.cofe.xsd.om;

import xyz.cofe.xsd.om.coll.ImList;
import xyz.cofe.xsd.om.xml.XmlDoc;
import xyz.cofe.xsd.om.xml.XmlElem;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.StreamSupport;

public class XsdDoc {
    public final XmlDoc xmlDoc;

    public final ImList<XsdInclude> includes;
    public final ImList<XsdImport> imports;

    public XsdDoc(XmlDoc xmlDoc) {
        if( xmlDoc==null ) throw new IllegalArgumentException("xmlDoc==null");
        this.xmlDoc = xmlDoc;

        this.includes =
            xmlDoc.getDocumentElement().walk().elems()
            .flatMap( elem ->
                XsdInclude.parse(elem)
                    .map(xsdInclude -> List.of(xsdInclude).iterator())
                    .orElse(Collections.emptyIterator()))
                .toImList();

        this.imports =
            xmlDoc.getDocumentElement().walk().elems()
            .flatMap( elem ->
                XsdImport.parse(elem)
                    .map(xsdInclude -> List.of(xsdInclude).iterator())
                    .orElse(Collections.emptyIterator()))
                .toImList();
    }
}
