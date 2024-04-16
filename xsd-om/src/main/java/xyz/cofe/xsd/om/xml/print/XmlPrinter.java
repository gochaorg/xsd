package xyz.cofe.xsd.om.xml.print;

import xyz.cofe.xsd.om.xml.XmlCData;
import xyz.cofe.xsd.om.xml.XmlDoc;
import xyz.cofe.xsd.om.xml.XmlElem;
import xyz.cofe.xsd.om.xml.XmlNode;
import xyz.cofe.xsd.om.xml.XmlText;

import java.io.IOException;

public class XmlPrinter {
    private final Appendable out;

    public XmlPrinter(Appendable out) {
        if (out == null) throw new IllegalArgumentException("out==null");
        this.out = out;
    }

    private void print(String str) {
        try {
            out.append(str);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void println(String str) {
        print(str);
        print("\n");
    }

    public void print(XmlDoc doc) {
        if (doc == null) throw new IllegalArgumentException("doc==null");
        print(doc.getDocumentElement(), 0);
    }

    public void print(XmlNode node, int level) {
        if (node == null) throw new IllegalArgumentException("node==null");

        if( node instanceof XmlElem el ){
            print(el,level);
        }else if( node instanceof XmlText el ){
            print(el,level);
        }else if( node instanceof XmlCData el ){
            print(el,level);
        }else {
            if (level > 0) print("  ".repeat(level));
            println("XmlNode"
                + " text=" + node.getTextContent()
            );
        }
    }

    public void print(XmlElem elem, int level) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        if (level > 0) print("  ".repeat(level));
        println("XmlElem"
            + " tagName=" + elem.getTagName()
            + " namespace=" + elem.getNamespaceURI()
            + " prefix=" + elem.getPrefix()
            + " localName=" + elem.getLocalName()
            + " attribs=" + elem.getAttributes()
            .map(a ->
                " a.name=" + a.getName()
                    + " a.localName=" + a.getLocalName()
                    + " a.nodeName=" + a.getNodeName()
                    + " a.namespace=" + a.getNamespaceURI()
                    + " a.prefix=" + a.getPrefix()
                    + " a.value=" + a.getValue())
            .foldLeft("", (acc, it) -> acc + " " + it)
            + " text=" + elem.getTextContent()
        );

        elem.getChildren().each( ch -> {
            print(ch, level + 1);
        });
    }

    public void print(XmlCData elem, int level) {
        if (elem == null) throw new IllegalArgumentException("elem==null");
        if (level > 0) print("  ".repeat(level));
        println("XmlCData"
            + " text=" + elem.getTextContent()
        );
    }

    public void print(XmlText text, int level) {
        if (text == null) throw new IllegalArgumentException("text==null");
        if (level > 0) print("  ".repeat(level));
        println("XmlText"
            + " text=" + text.getTextContent()
        );
    }
}
