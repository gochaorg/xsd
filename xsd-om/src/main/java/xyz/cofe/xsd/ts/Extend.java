package xyz.cofe.xsd.ts;

import xyz.cofe.im.struct.ImList;

/**
 * Унаследованный тип
 */
public interface Extend extends Type {
    /**
     * Возвращает базовый тип/типы
     * @return базовый тип/типы
     */
    ImList<Type> baseTypes();
}
