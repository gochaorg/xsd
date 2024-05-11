package xyz.cofe.ts;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.im.struct.Result;
import xyz.cofe.im.struct.Tuple2;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static xyz.cofe.im.struct.Result.ok;

/**
 * Структурный тип
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
 * </pre>
 *
 * <h2>Ограничения и подводные камни</h2>
 *
 * <ul>
 *     <li>
 *         Параметры типа {@link TypeParam} должны быть распределены по: базовым типам и/или полям
 *     </li>
 *     <li>
 *         Ограничения на {@link TypeParam} должны удовлетворять ограничениям в {@link GenericInstance}, в {@link #baseTypes()}
 *         - есть проверка в Run Time
 *     </li>
 *     <li>
 *         Можно запутаться между {@link TypeParam} конструируемого типа {@link Struct} и базового типа {@link #baseTypes()}
 *     </li>
 *     <li>
 *         В случае наследования одноименного поля {@link Field} необходимо смотреть на совместимость типа
 *     </li>
 *     <li>
 *         Нельзя допустить наличие двух или более одноименных полей в рамках одной структуры
 *     </li>
 * </ul>
 *
 * @see Type.SelfStruct
 */
public final class Struct implements ExtendType,
                                     GenericType,
                                     NamedType {
    public static class Builder {
        public final String name;

        public Builder(String name) {
            if (name == null) throw new IllegalArgumentException("name==null");
            this.name = name;
        }

        public Builder(Builder sample) {
            if (sample == null) throw new IllegalArgumentException("sample==null");
            this.name = sample.name;
            this.typeParams = sample.typeParams;
            this.baseTypes = sample.baseTypes;
            this.fields = sample.fields;
        }

        @SuppressWarnings("MethodDoesntCallSuperMethod")
        public Builder clone() {
            return new Builder(this);
        }

        //region typeParams : ImList<TypeParam>
        private ImList<TypeParam> typeParams = ImList.empty();

        public ImList<TypeParam> typeParams() {return typeParams;}

        public Builder typeParams(ImList<TypeParam> typeParams) {
            if (typeParams == null) throw new IllegalArgumentException("typeParams==null");

            var b = clone();
            b.typeParams = typeParams;
            return b;
        }

        public Builder typeParams(TypeParam... typeParams) {
            if (typeParams == null) throw new IllegalArgumentException("typeParams==null");
            var b = clone();
            b.typeParams = ImList.from(Arrays.asList(typeParams));
            return b;
        }

        //endregion
        //region baseTypes : ImList<Type>
        private ImList<Result<Type, String>> baseTypes = ImList.empty();

        public ImList<Result<Type, String>> baseTypes() {return baseTypes;}

        public Builder baseTypes(ImList<Result<Type, String>> baseTypes) {
            if (baseTypes == null) throw new IllegalArgumentException("baseTypes==null");
            var b = clone();
            b.baseTypes = baseTypes;
            return b;
        }

        public Builder baseType(Type baseType) {
            if (baseType == null) throw new IllegalArgumentException("baseType==null");
            var b = clone();
            b.baseTypes = ImList.first(ok(baseType));
            return b;
        }

        //endregion
        //region fields : ImList<Field>
        private ImList<Field> fields = ImList.empty();

        public ImList<Field> fields() {return this.fields;}

        public Builder fields(ImList<Field> fields) {
            if (fields == null) throw new IllegalArgumentException("fields==null");
            var b = clone();
            b.fields = fields;
            return b;
        }
        //endregion

        public Struct build() {
            return new Struct(
                name,
                typeParams,
                baseTypes,
                fields
            );
        }
    }

    public static Builder name(String name) {
        if (name == null) throw new IllegalArgumentException("name==null");
        return new Builder(name);
    }

    public Struct(String name, ImList<TypeParam> typeParams, ImList<Result<Type, String>> baseTypes, ImList<Field> fields) {
        if (fields == null) throw new IllegalArgumentException("fields==null");
        this.fields = fields;

        if (baseTypes == null) throw new IllegalArgumentException("baseTypes==null");
        this.baseTypes = baseTypes;

        if (name == null) throw new IllegalArgumentException("name==null");
        this.name = name;

        if (typeParams == null) throw new IllegalArgumentException("typeParams==null");
        this.typeParams = typeParams;
    }

    //region name : String
    private final String name;

    public String name() {
        return name;
    }

    @Override
    public Optional<String> baseName() {
        return Optional.of(name);
    }

    //endregion
    //region typeParams : ImList<TypeParam>
    private final ImList<TypeParam> typeParams;

    @Override
    public ImList<TypeParam> typeParams() {
        return typeParams;
    }

    //endregion
    //region baseTypes : ImList<Type>
    private final ImList<Result<Type, String>> baseTypes;

    @Override
    public ImList<Result<Type, String>> baseTypes() {
        return baseTypes;
    }

    //endregion
    //region fields : ImList<Field>
    private final ImList<Field> fields;

    public ImList<Field> fields() {
        return fields;
    }
    //endregion

    @Override
    public String getTypeName() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);

        NamedType.Names typeParamNames = new Names(typeParams);
        if (typeParams.isNonEmpty()) {
            sb.append(typeParamNames.toString());
        }

        var baseTypes = baseTypes()
            .map(bt0 -> bt0.fold(bt -> bt instanceof NamedWithContext nwCtx
                    ? nwCtx.getTypeName(typeParamNames)
                    : bt instanceof NamedType nt
                    ? nt.getTypeName() : "?",
                err -> "!base type err {" + err + "}")
            )
            .foldLeft("", (acc, it) -> !acc.isEmpty() ? acc + ", " + it : it);

        if (!baseTypes.isBlank()) {
            sb.append(" : ").append(baseTypes);
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return getTypeName();
    }
}
