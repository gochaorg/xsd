package xyz.cofe.ts;

import xyz.cofe.im.struct.ImList;

/**
 * Тип - "сумма", т.е. либо один тип, либо другой
 */
public record EitherType(ImList<Type> eitherTypes) implements Type {
}
