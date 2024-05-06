package xyz.cofe.ecoll;

import xyz.cofe.im.struct.Consumer3;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;

public class EvMap<K, V> {
    private final Map<K, V> map;

    public EvMap(Map<K, V> map) {
        if (map == null) throw new IllegalArgumentException("map==null");
        this.map = map;
    }

    public EvMap() {
        this.map = new HashMap<>();
    }

    public int size() {return map.size();}

    private final WeakHashMap<EvMapListener<K, V>, Boolean> weakListeners = new WeakHashMap<>();
    private final Set<EvMapListener<K, V>> strongListeners = new HashSet<>();

    public Runnable addListener(boolean weak, EvMapListener<K, V> listener) {
        if (listener == null) throw new IllegalArgumentException("listener==null");
        if (weak) {
            weakListeners.put(listener, true);
            WeakReference<EvMapListener<K, V>> wref = new WeakReference<>(listener);
            return () -> {
                var ref = wref.get();
                if (ref != null) {
                    weakListeners.remove(ref);
                }
            };
        } else {
            strongListeners.add(listener);
            return () -> {
                strongListeners.remove(listener);
            };
        }
    }

    public Runnable addListener(EvMapListener<K, V> listener) {
        if (listener == null) throw new IllegalArgumentException("listener==null");
        return addListener(false, listener);
    }

    public Runnable onInserted(BiConsumer<K, V> listener){
        if( listener==null ) throw new IllegalArgumentException("listener==null");
        return addListener( event -> {
            if( event instanceof EvMapEvent.Inserted<K,V> ev ){
                listener.accept(ev.key(), ev.item());
            }
        });
    }

    public Runnable onUpdated(Consumer3<K, V, V> listener){
        if( listener==null ) throw new IllegalArgumentException("listener==null");
        return addListener( event -> {
            if( event instanceof EvMapEvent.Updated<K,V> ev ){
                listener.accept(ev.key(), ev.old(), ev.item());
            }
        });
    }

    public Runnable onDeleted(BiConsumer<K, V> listener){
        if( listener==null ) throw new IllegalArgumentException("listener==null");
        return addListener( event -> {
            if( event instanceof EvMapEvent.Deleted<K,V> ev ){
                listener.accept(ev.key(), ev.old());
            }
        });
    }

    public Runnable onFullyChanged(Runnable listener){
        if( listener==null ) throw new IllegalArgumentException("listener==null");
        return addListener( event -> {
            if( event instanceof EvMapEvent.FullyChanged ){
                listener.run();
            }
        });
    }

    protected void fire(EvMapEvent<K, V> ev) {
        for (var ls : strongListeners) {
            if (ls != null) ls.evMapEvent(ev);
        }
        for (var ls : weakListeners.keySet()) {
            if (ls != null) ls.evMapEvent(ev);
        }
    }

    protected void fireInserted(K k, V v) {
        fire(new EvMapEvent.Inserted<>(k, v));
    }

    protected void fireUpdated(K k, V old, V cur) {
        fire(new EvMapEvent.Updated<>(k, cur, old));
    }

    protected void fireDeleted(K k, V v) {
        fire(new EvMapEvent.Deleted<>(k, v));
    }

    protected void fireFullyChanged() {
        fire(new EvMapEvent.FullyChanged<>());
    }

    public Optional<V> get(K key) {
        if (key == null) return Optional.empty();
        return Optional.ofNullable(map.get(key));
    }

    public Optional<V> put(K key, V value) {
        if (key == null) throw new IllegalArgumentException("key==null");
        V old = map.put(key, value);
        if (old != null) {
            fireUpdated(key, old, value);
        } else {
            fireInserted(key, value);
        }
        return Optional.ofNullable(old);
    }

    public Optional<V> delete(K key) {
        if (key == null) throw new IllegalArgumentException("key==null");
        var old = map.remove(key);

        map.remove(key, old);
        if (old != null) {
            fireDeleted(key, old);
        }
        return Optional.ofNullable(old);
    }

    public Optional<V> delete(K key, V value) {
        if (key == null) throw new IllegalArgumentException("key==null");
        if( map.remove(key, value) ) {
            if (value != null) {
                fireDeleted(key, value);
            }
            return Optional.ofNullable(value);
        }
        return Optional.empty();
    }

    public void clear(){
        map.clear();
        fireFullyChanged();
    }
}
