package xyz.cofe.ecoll;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class EvListTest {
    @Test
    public void test1(){
        var evs = new EvList<String>();

        var lst = new EvList<String>();
        lst.onInserted( (idx,a) -> {
            var msg = "ins "+idx+" "+a;
            System.out.println(msg);
            evs.insert(msg);
        });
        lst.onUpdated( (idx,old,cur) -> {
            var msg = "upd "+idx+" "+old+" -> "+cur;
            System.out.println(msg);
            evs.insert(msg);
        });
        lst.onDeleted( (idx,a) -> {
            var msg = "del "+idx+" "+a;
            System.out.println(msg);
            evs.insert(msg);
        });

        lst.insert("abc");
        System.out.println(lst.toImList());

        lst.insert("bcd");
        System.out.println(lst.toImList());

        lst.insert(0,"cde");
        System.out.println(lst.toImList());

        lst.update(0,"def");
        System.out.println(lst.toImList());

        lst.deleteAt(0);
        System.out.println(lst.toImList());

        lst.delete("bcd");
        System.out.println(lst.toImList());

        assertTrue( evs.get(0).map(s -> s.matches("ins 0 abc")).orElse(false) );
        assertTrue( evs.get(1).map(s -> s.matches("ins 1 bcd")).orElse(false) );
        assertTrue( evs.get(2).map(s -> s.matches("ins 0 cde")).orElse(false) );
        assertTrue( evs.get(3).map(s -> s.matches("upd 0 cde -> def")).orElse(false) );
        assertTrue( evs.get(4).map(s -> s.matches("del 0 def")).orElse(false) );
        assertTrue( evs.get(5).map(s -> s.matches("del 1 bcd")).orElse(false) );
    }
}
