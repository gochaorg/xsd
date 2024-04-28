package xyz.cofe.ts;

import xyz.cofe.im.struct.ImList;

/**
 * Тип "умножение" - сумма, т.е. некая структура данных с полями
 */
public interface StructType extends Type {
    record Field(String name, Type type) {}
    ImList<Field> fields();
}
