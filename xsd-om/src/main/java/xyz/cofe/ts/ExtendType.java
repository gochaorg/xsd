package xyz.cofe.ts;

import xyz.cofe.im.struct.ImList;

/**
 * Унаследованный тип
 */
public interface ExtendType extends Type {
    /**
     * Возвращает базовый тип/типы
     * @return базовый тип/типы
     */
    ImList<Type> baseTypes();
}
