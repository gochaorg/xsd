package xyz.cofe.ts;

/**
 * Тип данных
 */
public interface Type {
//    /**
//     * Проверка допустимости присвоения типы
//     * @param type тип
//     * @return true данные совместимы
//     */
//    boolean assignable( Type type );

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
