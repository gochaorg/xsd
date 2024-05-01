package xyz.cofe.ts;

/**
 * Примитивный тип
 */
public record PrimitiveType(String name) implements Type, NamedType {

    public static PrimitiveType of(String name){
        return new PrimitiveType(name);
    }

    @Override
    public String getTypeName() {
        return name;
    }
}
