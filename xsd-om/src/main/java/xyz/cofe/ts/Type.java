package xyz.cofe.ts;

import xyz.cofe.im.struct.Result;

/**
 * Тип данных
 */
public sealed interface Type extends TypeValue permits EitherType,
                                                       ExtendType,
                                                       GenericInstance,
                                                       GenericType,
                                                       PrimitiveType,
                                                       Type.SelfStruct,
                                                       TypeVar {
    /**
     * Проверка допустимости присвоения типы
     * @param type тип
     * @return true данные совместимы
     */
    default Result<Boolean,String> isAssignableFrom(Type type){
        if( type==null ) throw new IllegalArgumentException("type==null");
        return Result.ok(this == type);
    }

    /**
     * Специальный тип, для рекурсивной ссылки из field в struct:
     *
     * <p>Исходная структура</p>
     *
     * <pre>
     * struct StructName {
     *     fieldName : List&lt;<b style="color:blue">SelfStruct</b>&gt;
     * }
     * </pre>
     *
     * <p>Должно интерпретироваться как</p>
     *
     * <pre>
     * struct StructName {
     *     fieldName : List&lt;<b style="color:blue">StructName</b>&gt;
     * }
     * </pre>
     */
    public static final class SelfStruct implements Type {
        private SelfStruct() {}
        public final SelfStruct instance = new SelfStruct();
    }
}
