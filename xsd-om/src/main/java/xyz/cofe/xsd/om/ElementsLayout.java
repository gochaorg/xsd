package xyz.cofe.xsd.om;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.xsd.om.xml.XmlNode;

public sealed interface ElementsLayout permits XsdAll,
                                               XsdAny,
                                               XsdChoice,
                                               XsdGroup,
                                               XsdSequence {

    public interface NestedProperty extends ElemMethod {
        public default ImList<ElementsLayout> getNested(){ return elem().getChildren().flatMap(ElementsLayout::parseList); }
    }

    public interface NestedHolder extends NestedProperty, MinOccursAttribute, MaxOccursAttribute {}

    public static ImList<ElementsLayout> parse(ImList<XmlNode> nodes){
        if( nodes==null ) throw new IllegalArgumentException("nodes==null");
        return nodes.flatMap(n ->
            XsdAny.isMatch(n) ? XsdAny.parseList(n).map(a -> (ElementsLayout)a)
                : XsdAll.isMatch(n) ? XsdAny.parseList(n).map(a -> (ElementsLayout)a)
                : XsdChoice.isMatch(n) ? XsdChoice.parseList(n).map(a -> (ElementsLayout)a)
                : XsdGroup.isMatch(n) ? XsdGroup.parseList(n).map(a -> (ElementsLayout)a)
                : XsdSequence.isMatch(n) ? XsdSequence.parseList(n).map(a -> (ElementsLayout)a)
                : ImList.<ElementsLayout>empty()
        );
    }

    public static ImList<ElementsLayout> parseList(XmlNode node){
        if( node==null ) throw new IllegalArgumentException("node==null");
        return parse(ImList.first(node));
    }
}
