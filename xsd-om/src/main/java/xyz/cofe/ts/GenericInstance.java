package xyz.cofe.ts;

import xyz.cofe.im.struct.ImList;

import java.util.Objects;

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
public record GenericInstance(GenericType type, ImList<Type> typeValues)
    implements Type,
               NamedType.NamedWithContext,
               NamedType {
    public GenericInstance {
        Objects.requireNonNull(type);
        Objects.requireNonNull(typeValues);
    }

    @Override
    public String getTypeName() {
        if (typeValues.isEmpty()) {
            return type.baseName().orElse("?");
        }

        String baseName = type.baseName().orElse("?");
        String params = typeValues.map(tv ->
                tv instanceof NamedType nt
                    ? nt.getTypeName() : "?"
            )
            .foldLeft("", (acc, it) -> acc.isBlank() ? it : acc + ", " + it);

        return baseName + "<" + params + ">";
    }

    @Override
    public String getTypeName(TypeParamsName names) {
        if (names == null) throw new IllegalArgumentException("names==null");

        String baseName = type.baseName().orElse("?");
        String params = typeValues.map(tv ->
                tv instanceof NamedWithContext nwCtx
                    ? nwCtx.getTypeName(names)
                    : tv instanceof NamedType nt
                    ? nt.getTypeName()
                    : "?"
            )
            .foldLeft("", (acc, it) -> acc.isBlank() ? it : acc + ", " + it);

        return baseName + "<" + params + ">";
    }
}
