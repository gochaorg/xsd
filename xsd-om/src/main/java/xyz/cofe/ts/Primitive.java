package xyz.cofe.ts;

import java.util.Objects;

/**
 * Базовая реализация
 *
 * @param name имя типа
 */
public record Primitive(String name) implements PrimitiveType,
                                                NamedType {
    public Primitive {
        Objects.requireNonNull(name);
    }

    @Override
    public String getTypeName() {
        return name;
    }
}
