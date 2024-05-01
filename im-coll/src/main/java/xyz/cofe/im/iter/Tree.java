package xyz.cofe.im.iter;

import xyz.cofe.im.struct.ImList;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;

public class Tree<A> implements Iterator<TreePath<A>> {
    public static class TreeBuild<A> {
        private final Iterable<A> root;

        public TreeBuild(Iterable<A> root) {
            if (root == null) throw new IllegalArgumentException("root==null");
            this.root = root;
        }

        public ExtIterable<TreePath<A>> pathFollow(Function<TreePath<A>, ImList<A>> follow) {
            if (follow == null) throw new IllegalArgumentException("follow==null");
            return () -> new Tree<>(root, follow);
        }

        public ExtIterable<TreePath<A>> follow(Function<A, ImList<A>> follow) {
            if (follow == null) throw new IllegalArgumentException("follow==null");
            return () -> new Tree<>(root, p -> follow.apply(p.node()));
        }
    }

    public static <A> TreeBuild<A> root(A a) {
        return new TreeBuild<>(ImList.first(a));
    }

    public static <A> TreeBuild<A> root(A... a) {
        return new TreeBuild<>(ImList.from(Arrays.asList(a)));
    }

    public static <A> TreeBuild<A> root(Iterable<A> roots) {
        if (roots == null) throw new IllegalArgumentException("roots==null");
        return new TreeBuild<>(roots);
    }

    private ImList<TreePath<A>> workSet;
    private final Function<TreePath<A>, ImList<A>> follow;

    public Tree(Iterable<A> root, Function<TreePath<A>, ImList<A>> follow) {
        if (follow == null) throw new IllegalArgumentException("follow==null");
        this.follow = follow;

        if (root == null) throw new IllegalArgumentException("root==null");
        workSet = ImList.empty();
        for (var a : root) {
            workSet = workSet.prepend(TreePath.first(a));
        }
        workSet = workSet.reverse();
    }

    @Override
    public boolean hasNext() {
        return workSet.isNonEmpty();
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public TreePath<A> next() {
        if (workSet.isEmpty()) return null;

        var res = workSet.head().get();
        workSet = workSet.tail();

        ImList<TreePath<A>> follows =
            follow.apply(res).map(res::next);

        workSet = workSet.prepend(follows);
        return res;
    }
}
