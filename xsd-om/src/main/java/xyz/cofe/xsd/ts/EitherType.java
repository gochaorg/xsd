package xyz.cofe.xsd.ts;

import xyz.cofe.im.struct.ImList;

/**
 * Тип - "сумма", т.е. либо один тип, либо другой
 */
public interface EitherType extends Type {
    ImList<Type> eitherTypes();
}
