package xyz.cofe.ts;

import xyz.cofe.im.struct.ImList;

import java.util.function.Supplier;

public interface Generic {
    /**
     * Тип является параметризованным
     *
     * <h2>Условный пример</h2>
     * <pre>
     * MyType &lt; A, B: X, C: X+Y, +D, -E, +F: X+Y &gt; extends U &lt; A, C &gt;, V
     * </pre>
     *
     * <ul>
     *     <li>
     *         <b>MyType</b> - определяемый тип
     *     </li>
     *     <li>
     *         <b>А</b> - инвариантный тип, можно использовать как consumer (в параметрах), так и producer (результат)
     *     </li>
     *     <li>
     *         <b>B</b> - требование что бы тип расширял или являлся указанным типом X
     *     </li>
     *     <li>
     *         <b>C</b> - требование что бы тип расширял или являлся указанным типом X и Y
     *     </li>
     *     <li>
     *         <b>+D</b> - тип можно использовать как consumer в параметрах, ко-вариант
     *     </li>
     *     <li><b>-E</b> - тип можно использовать как producer в результате вызова, контр-вариант</li>
     *     <li><b>+F: X+Y</b> - Тип параметр, который должен реализовывать X и Y тип</li>
     *     <li>
     *         <b>U</b -> Generic тип с аргументами из MyType, аргументы являются позиционными
     *     </li>
     * </ul>
     */
    public interface GenericType extends Type {
        ImList<TypeParam> typeParams();
    }

    /**
     * Ко/Контр позиция
     */
    public enum CoPos {
        /**
         * Инвариант, использование и в аргументах/параметрах, и в результатах
         */
        InVariant,

        /**
         * Ко вариант, использование только в аргументах/параметрах
         *
         * <p></p>
         * Ковариантность - случай когда более конкретный тип S может быть подставлен вместо более обобщенного типа Т
         *
         * <p>
         *     Позиция параметры метода
         * </p>
         *
         * <p/>
         * <pre>
         * function f( a:S ) : T
         * </pre>
         *
         * S - super
         *
         * <ul>
         *     <li>S (super) - родительский тип</li>
         *     <li>T дочерний тип (T extends S)</li>
         *     <li>a - позиция, параметр является ко-вариантой, в место S - можно поставить T</li>
         *     <li>f(a):T - результат, контр-вариант
         *          <ul>
         *              <li>let b:T = f(a)</li>
         *              <li>в позицию результата (f(a):T) нельзя засунуть S</li>
         *          </ul>
         *     </li>
         * </ul>
         */
        Param,

        /** Контр вариант, использование только в результатах */
        Result
    }

    /**
     * Объявление параметра типа
     * @param constraints ограничения на тип
     * @param coPos вариантная позиция
     */
    record TypeParam(ImList<Type> constraints, CoPos coPos) {}

    /**
     * Ссылка на параметр
     *
     * @param typeParam параметр
     * @param paramOwner владелец параметра
     */
    record TypeVar(
        TypeParam typeParam,
        Supplier<GenericType> paramOwner
    ) implements Type {}

    /**
     * Тип экземпляра Generic.
     *
     * <h2>Условный пример</h2>
     * <pre>
     * MyType &lt; A, B: X, C: X+Y, +D, -E, +F: X+Y &gt; extends U <b> &lt; A, C &gt; </b> , V
     * </pre>
     *
     * Речь о двух параметрах (A, C) для U типа
     *
     * <p></p>
     *
     * <ul>
     *     <li>type должен ссылаться на U</li>
     *     <li>
     *         typeValues должен содержать ссылки либо
     *         <ul>
     *             <li>Конкретный тип</li>
     *             <li></li>
     *         </ul>
     *     </li>
     * </ul>
     * Параметр type - должен ссылаться на U
     *
     * @param type Какой Generic тип в качества основы
     * @param typeValues Последовательное значения параметров типа type
     */
    record GenericInstance(GenericType type, ImList<Type> typeValues) implements Type {}
}
