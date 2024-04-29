package xyz.cofe.ts;

import xyz.cofe.im.struct.ImList;

/**
 * Тип экземпляра Generic.
 *
 * <h2>Условный пример</h2>
 * <pre>
 * MyType &lt; A, B: X, C: X+Y, +D, -E, +F: X+Y &gt; extends U <b> &lt; A, C &gt; </b> , V
 * </pre>
 * <p>
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
 * @param type       Какой Generic тип в качества основы
 * @param typeValues Последовательное значения параметров типа type
 */
public record GenericInstance(GenericType type, ImList<Type> typeValues) implements Type {}
