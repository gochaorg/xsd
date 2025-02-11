package xyz.cofe.xml;

import xyz.cofe.coll.im.iter.ExtIterable;
import xyz.cofe.coll.im.ImList;

import java.util.Collections;
import java.util.List;

public interface XmlNode {
    String getTextContent();

    public static class Walk {
        private final XmlNode root;

        public Walk(XmlNode root) {
            if (root == null) throw new IllegalArgumentException("root==null");
            this.root = root;
        }

        public ExtIterable<ImList<XmlNode>> tree() {
            //noinspection unchecked
            return () -> new XmlNodeIterator(ImList.of(root));
        }

        public ExtIterable<XmlNode> nodes() {
            //noinspection OptionalGetWithoutIsPresent
            return tree()
                .map(revPath -> revPath.head().get());
        }

        public ExtIterable<XmlElem> elems() {
            return nodes().fmap(
                node -> node instanceof XmlElem e
                    ? List.of(e).iterator() : Collections.emptyIterator());
        }
    }

    default Walk walk() {return new Walk(this);}
}
