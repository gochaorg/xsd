package xyz.cofe.nixpath;

public sealed interface Name {
    String name();

    record Regular(String name) implements Name {
    }

    record ThisDir(String name) implements Name {}
    record ParentDir(String name) implements Name {}

    static Name of(String name) {
        if (name == null) throw new IllegalArgumentException("name==null");
        return switch (name) {
            case "." -> new ThisDir(name);
            case ".." -> new ParentDir(name);
            default -> new Regular(name);
        };
    }
}
