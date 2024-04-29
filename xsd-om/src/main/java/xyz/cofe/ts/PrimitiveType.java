package xyz.cofe.ts;

/**
 * Примитивный тип
 */
public interface PrimitiveType extends Type {

    static Primitive of(String name){
        return new Primitive(name);
    }

}
