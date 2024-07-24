package xyz.cofe.ts;

import xyz.cofe.coll.im.ImList;
import xyz.cofe.coll.im.Result;
import xyz.cofe.coll.im.Result.NoValue;

import java.util.Arrays;
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
               NamedType
{
    public GenericInstance(GenericType type, TypeValue ... typeValues ){
        this(type, ImList.from(Arrays.asList(typeValues)));
    }

    public GenericInstance {
        Objects.requireNonNull(type);
        Objects.requireNonNull(typeValues);

        var errOpt = validate(type, typeValues).getError();
        if( errOpt.isPresent() ){
            throw new RuntimeException(errOpt.get());
        }
    }

    public static Result<NoValue,String> validate(GenericType type, TypeValue ... typeValues){
        return validate(type, ImList.from(Arrays.asList(typeValues)));
    }

    public static Result<NoValue,String> validate(GenericType type, ImList<TypeValue> typeValues){
        if( type==null ) throw new IllegalArgumentException("type==null");
        if( typeValues==null ) throw new IllegalArgumentException("typeValues==null");

        if( type.typeParams().size() != typeValues.size() )
            return Result.error("typeValues count not match typeParameters count");

        ImList<Result<NoValue, String>> checkParams1 = type.typeParams().enumerate().zip(typeValues).map(ze -> {
            int tparamIdx = (int)ze._1().index();
            TypeParam tparamConsumer = ze._1().value();
            TypeValue tvalue = ze._2();

            return tparamConsumer.isAssignableFrom(tvalue).mapErr(err -> "type param#"+tparamIdx+"\n"+ err);
        });

        return checkParams1.foldLeft( Result.<String>ok(),
            (acc,it) ->
                acc.fold(
                    suc1 -> it,
                    err1 -> it.fold(
                        suc2 -> Result.error(err1),
                        err2 -> Result.error(err1 + "\n" + err2)
                    )
                )
        );
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
