package xyz.cofe.ts;

import xyz.cofe.im.struct.ImList;
import xyz.cofe.im.struct.Tuple2;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Именованный тип
 */
public interface NamedType {
    /**
     * Возвращает имя типа
     *
     * @return имя типа
     */
    public String getTypeName();

    public interface NamedWithContext {
        public String getTypeName(TypeParamsName names);
    }

    public interface TypeParamsName {
        public Optional<String> nameOf(TypeParam tp);
    }

    /**
     * Генерация имени для {@link TypeParam}
     */
    public static class Names implements TypeParamsName {
        public final ImList<TypeParam> typeParams;
        public final Map<TypeParam, String> names;
        public final Map<TypeParam, String> description;
        public final Map<TypeParam, String> nameAndDesc;

        public Names(ImList<TypeParam> typeParams) {
            if (typeParams == null) throw new IllegalArgumentException("typeParams==null");
            this.typeParams = typeParams;

            NumToSym numToSym = new NumToSym.NumToSymBasic();
            names = typeParams.enumerate().toMap(eidx -> Tuple2.of(eidx.value(), numToSym.numToSym(eidx.index())));
            description = typeParams.toMap(tp -> Tuple2.of(
                tp,

                tp.constraints().map(t ->
                    t instanceof GenericType gt ? gt.baseName().orElse("?")
                        : t instanceof NamedWithContext nwCtx
                        ? nwCtx.getTypeName(this)
                        : t instanceof NamedType nt
                        ? nt.getTypeName()
                        : "?"

                ).foldLeft("", (acc, it) -> !acc.isEmpty() ? acc + "+" + it : it)
            ));

            Map<TypeParam, String> nd = new HashMap<>();
            names.keySet().forEach(tp -> {
                String name = names.getOrDefault(tp, "");
                String desc = description.getOrDefault(tp, "");
                if (!name.isBlank()) {
                    if (!desc.isBlank()) {
                        nd.put(tp, name + ":" + desc);
                    } else {
                        nd.put(tp, name);
                    }
                }
            });
            nameAndDesc = nd;
        }

        @Override
        public Optional<String> nameOf(TypeParam tp) {
            if (tp == null) throw new IllegalArgumentException("tp==null");
            return Optional.ofNullable(names.get(tp));
        }

        public String toString() {
            if (nameAndDesc.isEmpty()) return "<>";
            return "<"
                + typeParams.map(tp ->
                    switch (tp.coPos()) {
                        case InVariant -> "";
                        case Param -> "+";
                        case Result -> "-";
                    } +
                        nameAndDesc.get(tp)
                )
                .foldLeft("", (acc, it) -> !acc.isEmpty() ? acc + ", " + it : it)
                + ">";
        }
    }
}
