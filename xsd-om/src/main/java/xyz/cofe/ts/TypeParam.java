package xyz.cofe.ts;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.im.struct.Tuple2;

import java.util.Map;

/**
 * Объявление параметра типа
 */
public final class TypeParam {
    public TypeParam(ImList<Type> constraints, CoPos coPos) {
        this.constraints = constraints;
        this.coPos = coPos;
    }

    private final ImList<Type> constraints;

    /**
     * Возвращает ограничения на тип
     * @return ограничения на тип
     */
    public ImList<Type> constraints(){
        return this.constraints;
    }

    private final CoPos coPos;

    /**
     * Возвращает вариантную позицию
     * @return вариантная позиция
     */
    public CoPos coPos(){
        return coPos;
    }
}
