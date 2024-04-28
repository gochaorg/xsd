package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.xml.XmlNode;

public sealed interface ElementsLayout permits XsdAll,
                                               XsdAny,
                                               XsdChoice,
                                               XsdGroup,
                                               XsdSequence,
                                               XsdElement {

    public non-sealed interface NestedProperty extends Xsd {
        public default ImList<ElementsLayout> getNested() {return elem().getChildren().flatMap(n -> ElementsLayout.parseList(n, this));}
    }

    public interface NestedHolder extends NestedProperty,
                                          MinOccursAttribute,
                                          MaxOccursAttribute {}

    public static ImList<ElementsLayout> parse(ImList<XmlNode> nodes, Xsd parent) {
        if (nodes == null) throw new IllegalArgumentException("nodes==null");
        return nodes.flatMap(n ->
            XsdAny.isMatch(n) ? XsdAny.parseList(n, parent).map(a -> (ElementsLayout) a)
                : XsdAll.isMatch(n) ? XsdAny.parseList(n, parent).map(a -> (ElementsLayout) a)
                : XsdChoice.isMatch(n) ? XsdChoice.parseList(n, parent).map(a -> (ElementsLayout) a)
                : XsdGroup.isMatch(n) ? XsdGroup.parseList(n, parent).map(a -> (ElementsLayout) a)
                : XsdSequence.isMatch(n) ? XsdSequence.parseList(n, parent).map(a -> (ElementsLayout) a)
                : ImList.<ElementsLayout>empty()
        );
    }

    public static ImList<ElementsLayout> parseList(XmlNode node, Xsd parent) {
        if (node == null) throw new IllegalArgumentException("node==null");
        return parse(ImList.first(node), parent);
    }
}
