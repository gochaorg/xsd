package xyz.cofe.ts;

import xyz.cofe.coll.im.ImList;

import java.util.Optional;

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
 *
 * <pre>
 * X {}
 * Y : X {} // Y ⇨ X
 *             1    0
 * Z : Y {} // Z ⇨ Y ⇨ X
 *             2   1    0
 *
 *  W &lt; A &gt; { a : A }
 * W1 &lt; A:X, B:Y &gt; : W &lt; A &gt; { b : B }
 * --↧------------------------------
 *   { a: A // наследование из W
 *   , b: B
 *   }
 * --↧------------------------------
 *   { a: X // Фактическое значение, ибо A=X
 *   , b: Y // Фактическое значение, ибо B=Y
 *   }
 *
 *  U &lt; +A:Y, -B:Y &gt; { a:A, b:B }
 *
 * U1 : U &lt; Z, X &gt; // Валидно
 *                 +A:Y Z <b style="color:green">⇨</b> Y       ( (+) конкретный ↔ общий (-) )
 *                      2(+)←1
 *                 -B:Y      Y <b style="color:green">⇨</b> X
 *                           1→(-)0
 *
 * U2 : U &lt; X, Z &gt; // Не валидно
 *                 +A:Y      Y <b style="color:red">⇨</b> X  ( (+) конкретный ↔ общий (-) )
 *                 -B:Y Z <b style="color:red">⇨</b> Y
 *
 * T1 : U &lt; Y, Y &gt;
 * T2 : U &lt; Y, Y &gt;
 *
 * ⇠ - <i>assign, присвоение</i>
 *
 * T1 ⇠ T2 // ok, потому что Y ⇠ Y, Y ⇠ Y
 * T2 ⇠ T1 // ok, потому что Y ⇠ Y, Y ⇠ Y
 *
 * U &lt; Y, Y &gt; ⇠(assign)⇠ U &lt; Z, Y &gt; // допустимо +A:Y, (+) Z <b style="color:green">⇨</b> Y
 * U &lt; Z, Y &gt; ⇠(assign)⇠ U &lt; Z, Y &gt; // допустимо
 * U &lt; Z, Y &gt; <b style="color:red">⇠(not a.)⇠</b> U &lt; Y, Y &gt; // не допустимо Z <b style="color:red">⇠</b> Y
 * U &lt; Y, Y &gt; ⇠(assign)⇠ U &lt; Y, X &gt; // допустимо -B:Y  Y <b style="color:green">⇨</b> (-) X
 * </pre>
 *
 */
public sealed interface GenericType extends Type permits Struct {
    ImList<TypeParam> typeParams();

    default Optional<String> baseName(){ return Optional.empty(); }
}
