package xyz.cofe.ts;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.im.struct.Result;

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
public record GenericInstance(GenericType type, ImList<TypeValue> typeValues)
    implements Type,
               NamedType.NamedWithContext,
               NamedType {

    public GenericInstance {
        Objects.requireNonNull(type);
        Objects.requireNonNull(typeValues);
    }

    public static Result<Void,String> validate(GenericType type, ImList<Type> typeValues){
        if( type==null ) throw new IllegalArgumentException("type==null");
        if( typeValues==null ) throw new IllegalArgumentException("typeValues==null");

        if( type.typeParams().size() != typeValues.size() )
            return Result.err("typeValues count not match typeParameters count");

        Void noErr = Void.TYPE.cast(GenericInstance.class);

        type.typeParams().enumerate().zip(typeValues).map( ze -> {
            int tparamIdx = ze.left().index();
            TypeParam tparamConsumer = ze.left().value();
            Type tvalue = ze.right();

            if( tvalue instanceof TypeVar tv ){
                var prduce = tv.typeParam();
            }

//            if( tparamConsumer.constraints().isEmpty() )return Result.ok(noErr);
//
//            tparamConsumer.constraints().map( constraintType -> {
//            });

            // TODO !!!

            return Result.ok(noErr);
        });

        return Result.ok(Void.TYPE.cast(GenericInstance.class));
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
