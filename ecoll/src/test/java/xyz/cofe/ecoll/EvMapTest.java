package xyz.cofe.ecoll;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class EvMapTest {
    @Test
    public void test(){
        EvMap<String,String> map = new EvMap<>();

        var evs = new EvList<String>();

        map.onInserted( (idx,a) -> {
            var msg = "ins "+idx+" "+a;
            System.out.println(msg);
            evs.insert(msg);
        });
        map.onUpdated( (idx,old,cur) -> {
            var msg = "upd "+idx+" "+old+" -> "+cur;
            System.out.println(msg);
            evs.insert(msg);
        });
        map.onDeleted( (idx,a) -> {
            var msg = "del "+idx+" "+a;
            System.out.println(msg);
            evs.insert(msg);
        });

        map.put("0","abc");
        map.put("1","bcd");
        map.put("0","cde");
        map.delete("0");
        var deleted = map.delete("1","xyz");
        assertTrue(deleted.isEmpty());

        assertTrue( evs.get(0).map(s -> s.matches("ins 0 abc")).orElse(false) );
        assertTrue( evs.get(1).map(s -> s.matches("ins 1 bcd")).orElse(false) );
        assertTrue( evs.get(2).map(s -> s.matches("upd 0 abc -> cde")).orElse(false) );
        assertTrue( evs.get(3).map(s -> s.matches("del 0 cde")).orElse(false) );
    }
}
