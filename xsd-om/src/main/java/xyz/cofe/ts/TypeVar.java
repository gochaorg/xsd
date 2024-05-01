package xyz.cofe.ts;

import java.util.function.Supplier;

/**
 * Ссылка на параметр
 *
 * @param typeParam параметр
 */
public record TypeVar(
    TypeParam typeParam
) implements Type,
             NamedType.NamedWithContext,
             NamedType {
    @Override
    public String getTypeName(NamedType.TypeParamsName names) {
        if (names == null) throw new IllegalArgumentException("names==null");
        var tpNameOpt = names.nameOf(typeParam);
        if (tpNameOpt.isPresent()) {
            return tpNameOpt.get();
        }

        if (typeParam.constraints().isNonEmpty()) {
            var constr = typeParam.constraints().map(tc ->
                    tc instanceof NamedWithContext nwCtx
                        ? nwCtx.getTypeName(names)
                        : tc instanceof NamedType nt
                        ? nt.getTypeName() : "?"
                )
                .foldLeft("", (acc, it) -> acc.isEmpty() ? it : acc + "+" + it);

            return "?:" + constr;
        }

        return "?";
    }

    @Override
    public String getTypeName() {
        if (typeParam.constraints().isNonEmpty()) {
            var constr = typeParam.constraints().map(tc ->
                    tc instanceof NamedType nt
                        ? nt.getTypeName() : "?"
                )
                .foldLeft("", (acc, it) -> acc.isEmpty() ? it : acc + "+" + it);

            return "?:" + constr;
        }

        return "?";
    }
}
