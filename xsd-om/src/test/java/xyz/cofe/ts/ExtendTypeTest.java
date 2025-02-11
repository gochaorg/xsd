package xyz.cofe.ts;

import org.junit.jupiter.api.Test;
import xyz.cofe.coll.im.ImList;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExtendTypeTest {
    @Test
    public void test1() {
        var s1 = Struct.name("S1").build();
        var s2 = Struct.name("S2").baseType(s1).build();
        var s3 = Struct.name("S3").baseType(s2).build();

        assertTrue(s1.isAssignableFrom(s1).toOptional().orElse(false) );
        assertTrue(s1.isAssignableFrom(s2).toOptional().orElse(false));
        assertTrue(s1.isAssignableFrom(s3).toOptional().orElse(false));

        assertTrue(!s2.isAssignableFrom(s1).toOptional().orElse(false));
        assertTrue(s2.isAssignableFrom(s2).toOptional().orElse(false));
        assertTrue(s2.isAssignableFrom(s3).toOptional().orElse(false));

        assertTrue(!s3.isAssignableFrom(s1).toOptional().orElse(false));
        assertTrue(!s3.isAssignableFrom(s2).toOptional().orElse(false));
        assertTrue(s3.isAssignableFrom(s3).toOptional().orElse(false));
    }

    @Test
    public void genericInstance() {
        var X = Struct.name("X").build();
        var Y = Struct.name("Y").baseType(X).build();
        var Z = Struct.name("Z").baseType(Y).build();

        var U = Struct.name("U")
            .typeParams(
                TypeParam.createParam(Y),
                TypeParam.createResult(Y)
            )
            .build();

        System.out.println(U.toString());

        var v_tp1 = new TypeParam(ImList.of(Z), CoPos.Param);
        var v_tp2 = new TypeParam(ImList.of(X), CoPos.Result);

        var tv1 = new TypeVar(v_tp1);
        var tv2 = new TypeVar(v_tp2);

        System.out.println("-----");

        var validation1 = GenericInstance.validate(U, tv1, tv2);
        System.out.println(validation1);
        assertTrue(validation1.isOk());

        System.out.println("-----");

        var validation2 = GenericInstance.validate(U, tv2, tv1);
        System.out.println(validation2);
        assertTrue(validation2.isError());

        System.out.println("-----");

        var validation3 = GenericInstance.validate(U,
            new TypeVar(TypeParam.createParam(Y)),
            new TypeVar(TypeParam.createResult(Y))
        );
        System.out.println(validation3);
        assertTrue(validation3.isOk());

        System.out.println("-----");

        var validation4 = GenericInstance.validate(U,
            new TypeVar(TypeParam.createParam(X)),
            new TypeVar(TypeParam.createResult(Y))
        );
        System.out.println(validation4);
        assertTrue(validation4.isError());

        System.out.println("-----");

        var gi = new GenericInstance(U, tv1, tv2);

        var V = Struct.name("V")
            .typeParams(v_tp1,v_tp2)
            .baseType(gi)
            .build();

        var v_name = V.getTypeName();
        System.out.println(v_name);
    }
}
