package xyz.cofe.xsd.ui.ev;

import xyz.cofe.ecoll.EvListEvent;
import xyz.cofe.ecoll.EvListListener;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;

public class EvProp<A> {
    private A value;

    public EvProp(){
    }

    public EvProp(A initial){
        this.value = initial;
    }

    private final WeakHashMap<EvPropListener<A>,Boolean> weakListeners = new WeakHashMap<>();
    private final Set<EvPropListener<A>> strongListeners = new HashSet<>();

    public void clearListeners(){
        weakListeners.clear();
        strongListeners.clear();
    }

    public Runnable addListener(EvPropListener<A> listener) {
        if( listener==null ) throw new IllegalArgumentException("listener==null");
        return addListener(false,listener);
    }

    public Runnable addListener(boolean weak, EvPropListener<A> listener){
        if( listener==null ) throw new IllegalArgumentException("listener==null");
        if( weak ){
            weakListeners.put(listener,true);
            var wref = new WeakReference<>(listener);
            return () -> {
                var ref = wref.get();
                if( ref!=null ){
                    weakListeners.remove(ref);
                }
            };
        }else{
            strongListeners.add(listener);
            return ()->{
                strongListeners.remove(listener);
            };
        }
    }

    public Runnable onChanged(BiConsumer<A,A> listener){
        if( listener==null ) throw new IllegalArgumentException("listener==null");
        return addListener(false, event -> {
            if( event instanceof EvPropEvent.Changed<A> ch ){
                listener.accept(ch.from(), ch.to());
            }
        });
    }

    protected void fireChanged(A from, A to){
        fire(new EvPropEvent.Changed<>(from, to));
    }

    protected void fire(EvPropEvent<A> ev){
        for( var ls : strongListeners ){
            if( ls!=null )ls.evPropEvent(ev);
        }
        for( var ls : weakListeners.keySet() ){
            if( ls!=null )ls.evPropEvent(ev);
        }
    }

    public void setValue(A value){
        A from = this.value;
        this.value = value;
        A to = this.value;
        fireChanged(from, to);
    }

    public A getValue(){
        return this.value;
    }
}
