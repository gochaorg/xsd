package xyz.cofe.ts;

import org.junit.jupiter.api.Test;
import xyz.cofe.im.struct.ImList;

public class ExtendTypeTest {
    private Struct s1 = Struct.name("S1").build();
    private Struct s2 = Struct.name("S2").baseType(s1).build();
    private Struct s3 = Struct.name("S3").baseType(s2).build();

    @Test
    public void test1() {
        boolean s1_a_s2 = s1.isAssignableFrom(s2);
        System.out.println(s1_a_s2);
    }

    @Test
    public void testName() {
        var X = Struct.name("X").build();
        var Y = Struct.name("Y").baseType(X).build();
        var Z = Struct.name("Z").baseType(Y).build();

        var tp1 = new TypeParam(ImList.first(Y), CoPos.Param);
        var tp2 = new TypeParam(ImList.first(Y), CoPos.Result);

        var U = Struct.name("U")
            .typeParams(
                ImList.first(tp1)
                    .append(tp2)
            )
            .build();

        System.out.println(U.toString());

        var v_tp1 = new TypeParam(ImList.first(Z), CoPos.Param);
        var v_tp2 = new TypeParam(ImList.first(X), CoPos.Result);

        var tv1 = new TypeVar(v_tp1);
        var tv2 = new TypeVar(v_tp2);

        var gi = new GenericInstance(U, ImList.first(tv1).append(tv2).map(a->a));

        var V = Struct.name("V")
            .typeParams(ImList.first(v_tp1).append(v_tp2))
            .baseType(gi)
            .build();

        var v_name = V.getTypeName();
        System.out.println(v_name);
    }
}
