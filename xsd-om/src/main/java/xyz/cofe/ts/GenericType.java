package xyz.cofe.ts;

import xyz.cofe.im.struct.ImList;

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
