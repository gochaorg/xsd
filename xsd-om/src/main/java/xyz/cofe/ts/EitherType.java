package xyz.cofe.ts;

import xyz.cofe.coll.im.ImList;

/**
 * Тип - "сумма", т.е. либо один тип, либо другой
 */
public record EitherType(ImList<Type> eitherTypes) implements Type {
}
