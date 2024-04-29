package xyz.cofe.ts;

import java.util.function.Supplier;

/**
 * Ссылка на параметр
 *
 * @param typeParam  параметр
 * @param paramOwner владелец параметра
 */
public record TypeVar(
    TypeParam typeParam,
    Supplier<GenericType> paramOwner
) implements Type {}
