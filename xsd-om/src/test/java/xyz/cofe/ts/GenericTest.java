package xyz.cofe.ts;

import org.junit.jupiter.api.Test;
import xyz.cofe.im.struct.ImList;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GenericTest {
    @Test
    public void num2sym(){
        var n2s = new NumToSym.NumToSymBasic();

        System.out.println(n2s.numToSym(0));
        assertTrue( "A".equals(n2s.numToSym(0)) );

        System.out.println(n2s.numToSym(1));
        assertTrue( "B".equals(n2s.numToSym(1)) );

        System.out.println(n2s.numToSym(2));
        assertTrue( "C".equals(n2s.numToSym(2)) );

        System.out.println(n2s.numToSym(25));
        assertTrue( "Z".equals(n2s.numToSym(25)) );

        System.out.println(n2s.numToSym(26));
        assertTrue( "BA".equals(n2s.numToSym(26)) );

        System.out.println(n2s.numToSym(27));
        assertTrue( "BB".equals(n2s.numToSym(27)) );
    }

    Primitive str = PrimitiveType.of("String");
    Primitive bool = PrimitiveType.of("Boolean");

    @Test
    public void test1(){
        TypeParam tp1 = new TypeParam(
            ImList.first(str),
            CoPos.InVariant
        );

        Generic list = new Generic(
            "List",
            ImList.first(tp1),
            ImList.empty()
        );

        System.out.println(list.getTypeName());
        assertTrue("List<A:String>".equals(list.getTypeName()));
    }

    @Test
    public void test2(){
        TypeParam tp1 = new TypeParam(
            ImList.first(str).append(bool).map(a -> a),
            CoPos.InVariant
        );

        Generic list = new Generic(
            "List",
            ImList.first(tp1),
            ImList.empty()
        );

        System.out.println(list.getTypeName());
        assertTrue("List<A:String+Boolean>".equals(list.getTypeName()));
    }

    @Test
    public void test3(){
        TypeParam tp1 = new TypeParam(
            ImList.first(str),
            CoPos.InVariant
        );

        TypeParam tp2 = new TypeParam(
            ImList.first(bool),
            CoPos.InVariant
        );

        Generic list = new Generic(
            "List",
            ImList.first(tp1).append(tp2),
            ImList.empty()
        );

        System.out.println(list.getTypeName());
        assertTrue("List<A:String, B:Boolean>".equals(list.getTypeName()));
    }

    @Test
    public void test4(){
        TypeParam tp1 = new TypeParam(
            ImList.first(str),
            CoPos.Param
        );

        TypeParam tp2 = new TypeParam(
            ImList.first(bool),
            CoPos.Result
        );

        Generic list = new Generic(
            "List",
            ImList.first(tp1).append(tp2),
            ImList.empty()
        );

        System.out.println(list.getTypeName());
        assertTrue("List<+A:String, -B:Boolean>".equals(list.getTypeName()));
    }
}
