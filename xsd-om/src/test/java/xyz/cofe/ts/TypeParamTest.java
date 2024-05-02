package xyz.cofe.ts;

import org.junit.jupiter.api.Test;
import xyz.cofe.im.struct.ImList;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TypeParamTest
{
    Struct X = Struct.name("X").build();
    Struct Y = Struct.name("Y").baseType(X).build();
    Struct Z = Struct.name("Z").baseType(Y).build();
    Struct I = Struct.name("I").build();

    TypeParam u_tp_y_param = new TypeParam(ImList.first(Y), CoPos.Param);
    TypeParam u_tp_y_res = new TypeParam(ImList.first(Y), CoPos.Result);

    Struct U = Struct.name("U")
        .typeParams(u_tp_y_param,u_tp_y_res)
        .build();

    TypeParam tp_z_param = new TypeParam(ImList.first(Z), CoPos.Param);
    TypeParam tp_x_res = new TypeParam(ImList.first(X), CoPos.Result);

    TypeVar tv_z_param = new TypeVar(tp_z_param);
    TypeVar tv_x_res = new TypeVar(tp_x_res);

    GenericInstance gi1 = new GenericInstance(U, ImList.first(tv_z_param).append(tv_x_res).map(a -> a));

    Struct V1 = Struct.name("V1")
        .typeParams(tp_z_param,tp_x_res)
        .baseType(gi1)
        .build();

    TypeParam tp_zi_param = new TypeParam(ImList.first(Z).append(I).map(a->a), CoPos.Param);

    TypeVar v2_tv_zi_param = new TypeVar(tp_zi_param);
    TypeVar v2_tv_x_res = new TypeVar(tp_x_res);

    GenericInstance gi2 = new GenericInstance(U, ImList.first(v2_tv_zi_param).append(v2_tv_x_res).map(a -> a));

    Struct V2 = Struct.name("V2")
        .typeParams(tp_zi_param,tp_x_res)
        .baseType(gi2)
        .build();

    @Test
    public void test2(){
        var v_tp1b_y = u_tp_y_param.isAssignableFrom(v2_tv_zi_param);
        System.out.println(v_tp1b_y);
        assertTrue(v_tp1b_y.isOk());
    }

    @Test
    public void typeParamAssignTest(){
        System.out.println(U.toString());

        var v_name1 = V1.getTypeName();
        System.out.println(v_name1);

        var v_name2 = V2.getTypeName();
        System.out.println(v_name2);

        var u_tp1_x = u_tp_y_param.isAssignableFrom(X);
        System.out.println(u_tp1_x);
        assertTrue(u_tp1_x.isErr());

        var u_tp1_y = u_tp_y_param.isAssignableFrom(Y);
        System.out.println(u_tp1_y);
        assertTrue(u_tp1_y.isOk());

        var u_tp1_z = u_tp_y_param.isAssignableFrom(Z);
        System.out.println(u_tp1_z);
        assertTrue(u_tp1_z.isOk());

        var v_tp_y__z_param = u_tp_y_param.isAssignableFrom(tv_z_param);
        System.out.println(v_tp_y__z_param);
        assertTrue(v_tp_y__z_param.isOk());
    }
}
