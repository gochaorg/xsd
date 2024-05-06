package xyz.cofe.ecoll;

import xyz.cofe.im.struct.Consumer3;
import xyz.cofe.im.struct.ImList;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class EvList<A> {
    private final List<A> list;

    public EvList(List<A> list) {
        if( list==null ) throw new IllegalArgumentException("list==null");
        this.list = list;
    }

    public EvList(){
        this.list = new ArrayList<>();
    }

    private final WeakHashMap<EvListListener<A>,Boolean> weakListeners = new WeakHashMap<>();
    private final Set<EvListListener<A>> strongListeners = new HashSet<>();

    public Runnable addListener(boolean weak, EvListListener<A> listener){
        if( listener==null ) throw new IllegalArgumentException("listener==null");
        if( weak ){
            weakListeners.put(listener,true);
            WeakReference<EvListListener<A>> wref = new WeakReference<>(listener);
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

    public Runnable addListener(EvListListener<A> listener) {
        if( listener==null ) throw new IllegalArgumentException("listener==null");
        return addListener(false,listener);
    }

    public Runnable onInserted(BiConsumer<Integer, A> listener){
        if( listener==null ) throw new IllegalArgumentException("listener==null");
        return addListener( event -> {
            if( event instanceof EvListEvent.Inserted<A> ev ){
                listener.accept(ev.index(), ev.item());
            }
        });
    }

    public Runnable onUpdated(Consumer3<Integer, A, A> listener){
        if( listener==null ) throw new IllegalArgumentException("listener==null");
        return addListener( event -> {
            if( event instanceof EvListEvent.Updated<A> ev ){
                listener.accept(ev.index(), ev.old(), ev.item());
            }
        });
    }

    public Runnable onDeleted(BiConsumer<Integer, A> listener){
        if( listener==null ) throw new IllegalArgumentException("listener==null");
        return addListener( event -> {
            if( event instanceof EvListEvent.Deleted<A> ev ){
                listener.accept(ev.index(), ev.old());
            }
        });
    }

    public Runnable onFullyChanged(Runnable listener){
        if( listener==null ) throw new IllegalArgumentException("listener==null");
        return addListener( event -> {
            if( event instanceof EvListEvent.FullyChanged ){
                listener.run();
            }
        });
    }

    protected void fire(EvListEvent<A> ev){
        for( var ls : strongListeners ){
            if( ls!=null )ls.evListEvent(ev);
        }
        for( var ls : weakListeners.keySet() ){
            if( ls!=null )ls.evListEvent(ev);
        }
    }

    protected void fireInserted(int idx, A a){
        fire(new EvListEvent.Inserted<>(idx,a));
    }

    protected void fireUpdated(int idx, A old, A cur){
        fire(new EvListEvent.Updated<>(idx,cur,old));
    }

    protected void fireDeleted(int idx, A old){
        fire(new EvListEvent.Deleted<>(idx,old));
    }

    protected void fireFullyChanged(){
        fire(new EvListEvent.FullyChanged<>());
    }

    public int size(){
        return list.size();
    }

    public Optional<A> get(int index){
        if( index<0 )return Optional.empty();
        if( index>=size() )return Optional.empty();
        return Optional.ofNullable(list.get(index));
    }

    public Optional<A> update(int index,A item){
        if( index<0 )return Optional.empty();
        if( index>=size() )return Optional.empty();
        var prev = list.set(index,item);
        fireUpdated(index,prev,item);
        return Optional.ofNullable(prev);
    }

    public void insert(int index,A item){
        if( index>=size() ){
            insert(item);
            return;
        }

        var tidx = Math.max(index, 0);
        list.add(tidx, item);

        fireInserted(tidx, item);
    }

    public void insert(A item){
        list.add(item);
        fireInserted(list.size()-1, item);
    }

    public Optional<A> delete(A item){
        var idx = list.indexOf(item);
        if( idx<0 )return Optional.empty();

        var old = list.remove(idx);
        fireDeleted(idx, old);

        return Optional.ofNullable(old);
    }

    public void deleteAt(int index){
        if( index<0 || index>=size() )return;
        var old = list.remove(index);

        fireDeleted(index, old);
    }

    public void clear(){
        list.clear();
        fireFullyChanged();
    }

    public ImList<A> toImList(){
        return ImList.from(list);
    }
}
