package xyz.cofe.ecoll;

import xyz.cofe.coll.im.iter.ExtIterable;
import xyz.cofe.coll.im.Consumer3;
import xyz.cofe.coll.im.ImList;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Изменяемый список с поддержкой уведомлений об изменении.
 *
 * <p></p>
 * Работает и в JVM и TeaVM
 * @param <A> Тип элемента
 */
public class EvList<A> implements ExtIterable<A> {
    private final List<A> list;

    /**
     * Конструктор
     * @param list исходный список
     */
    public EvList(List<A> list) {
        if( list==null ) throw new IllegalArgumentException("list==null");
        this.list = list;
    }

    /**
     * Конструктор
     */
    public EvList(){
        this.list = new ArrayList<>();
    }

    private final WeakHashMap<EvListListener<A>,Boolean> weakListeners = new WeakHashMap<>();
    private final Set<EvListListener<A>> strongListeners = new HashSet<>();

    /**
     * Добавление подписчика
     * @param weak true - добавить подписчика как weak ссылку
     * @param listener подписчик
     * @return Отписка от уведомлений
     */
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

    /**
     * Добавление подписчика
     * @param listener подписчик
     * @return Отписка от уведомлений
     */
    public Runnable addListener(EvListListener<A> listener) {
        if( listener==null ) throw new IllegalArgumentException("listener==null");
        return addListener(false,listener);
    }

    /**
     * Добавление подписчика на событие insert
     * @param listener подписчик
     * @return Отписка от уведомлений
     */
    public Runnable onInserted(BiConsumer<Integer, A> listener){
        if( listener==null ) throw new IllegalArgumentException("listener==null");
        return addListener( event -> {
            if( event instanceof EvListEvent.Inserted<A> ev ){
                listener.accept(ev.index(), ev.item());
            }
        });
    }

    /**
     * Добавление подписчика на событие {@link EvListEvent.Updated}
     * @param listener подписчик: fn(индекс, предыдущий, новый элемент)
     * @return Отписка от уведомлений
     */
    public Runnable onUpdated(Consumer3<Integer, A, A> listener){
        if( listener==null ) throw new IllegalArgumentException("listener==null");
        return addListener( event -> {
            if( event instanceof EvListEvent.Updated<A> ev ){
                listener.accept(ev.index(), ev.old(), ev.item());
            }
        });
    }

    /**
     * Добавление подписчика на событие {@link EvListEvent.Deleted}
     * @param listener подписчик
     * @return Отписка от уведомлений
     */
    public Runnable onDeleted(BiConsumer<Integer, A> listener){
        if( listener==null ) throw new IllegalArgumentException("listener==null");
        return addListener( event -> {
            if( event instanceof EvListEvent.Deleted<A> ev ){
                listener.accept(ev.index(), ev.old());
            }
        });
    }

    /**
     * Добавление подписчика на событие {@link EvListEvent.FullyChanged}
     * @param listener подписчик
     * @return Отписка от уведомлений
     */
    public Runnable onFullyChanged(Runnable listener){
        if( listener==null ) throw new IllegalArgumentException("listener==null");
        return addListener( event -> {
            if( event instanceof EvListEvent.FullyChanged ){
                listener.run();
            }
        });
    }

    /**
     * Добавление подписчика на любое изменение списка
     * @param listener подписчик
     * @return Отписка от уведомлений
     */
    @SuppressWarnings("UnusedReturnValue")
    public Runnable onChanged(Runnable listener){
        if( listener==null ) throw new IllegalArgumentException("listener==null");
        var ins = onInserted((a,b)->listener.run());
        var upd = onUpdated((a,b,c)->listener.run());
        var del = onDeleted((a,b)->listener.run());
        var fc = onFullyChanged(listener);
        return ()->{
            ins.run();
            upd.run();
            del.run();
            fc.run();
        };
    }

    /**
     * Рассылка уведомления подписчикам
     * @param ev уведомление
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected void fire(EvListEvent<A> ev){
        // унифицировать до for( var ls : strongListeners ) - нельзя ошибка teavm
        EvListListener[] listeners = strongListeners.toArray(new EvListListener[]{});
        for(EvListListener<A> ls : listeners ){
            if( ls!=null )ls.evListEvent(ev);
        }

        listeners = weakListeners.keySet().toArray(new EvListListener[]{});
        for( var ls : listeners ){
            if( ls!=null )ls.evListEvent(ev);
        }
    }

    /**
     * Генерирует событие {@link EvListEvent.Inserted}
     * @param idx индекс
     * @param a элемент
     */
    protected void fireInserted(int idx, A a){
        fire(new EvListEvent.Inserted<>(idx,a));
    }

    /**
     * Генерирует событие {@link EvListEvent.Updated}
     * @param idx индекс
     * @param old предыдущий элемент
     * @param cur новый элемент
     */
    protected void fireUpdated(int idx, A old, A cur){
        fire(new EvListEvent.Updated<>(idx,cur,old));
    }

    /**
     * Генерирует событие {@link EvListEvent.Deleted}
     * @param idx индекс
     * @param old предыдущий элемент
     */
    protected void fireDeleted(int idx, A old){
        fire(new EvListEvent.Deleted<>(idx,old));
    }

    /**
     * Генерирует событие {@link EvListEvent.FullyChanged}
     */
    protected void fireFullyChanged(){
        fire(new EvListEvent.FullyChanged<>());
    }

    /**
     * Возвращает кол-во элементов в списке
     * @return кол-во элементов
     */
    public int size(){
        return list.size();
    }

    /**
     * Возвращает элементо по его индексу
     * @param index индекс: 0 - первый элемент списка
     * @return элемент или Optional.empty()
     */
    public Optional<A> get(int index){
        if( index<0 )return Optional.empty();
        if( index>=size() )return Optional.empty();
        return Optional.ofNullable(list.get(index));
    }

    /**
     * Обновляет заданный элемент.
     *
     * <p></p>
     * Генерирует событие {@link EvListEvent.Updated}
     *
     * @param index индекс элемента
     * @param item новый элемент
     * @return предыдущий
     */
    @SuppressWarnings("UnusedReturnValue")
    public Optional<A> update(int index, A item){
        if( index<0 )return Optional.empty();
        if( index>=size() )return Optional.empty();
        var prev = list.set(index,item);
        fireUpdated(index,prev,item);
        return Optional.ofNullable(prev);
    }

    /**
     * Добавление элемента в указанную позицию списка
     *
     * <p></p>
     * Генерирует событие {@link EvListEvent.Inserted}
     *
     * @param index позиция: 0 - начало списка
     * @param item элемент
     */
    public void insert(int index,A item){
        if( index>=size() ){
            insert(item);
            return;
        }

        var tidx = Math.max(index, 0);
        list.add(tidx, item);

        fireInserted(tidx, item);
    }

    /**
     * Добавление элемента в конец списка
     * <p></p>
     * Генерирует событие {@link EvListEvent.Inserted}
     *
     * @param item элемент
     */
    public void insert(A item){
        list.add(item);
        fireInserted(list.size()-1, item);
    }

    /**
     * Удалят элемент из списка
     * <p></p>
     * Генерирует событие {@link EvListEvent.Deleted}
     *
     * @param item элемент
     * @return элемент или Optional.empty()
     */
    @SuppressWarnings("UnusedReturnValue")
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

    /**
     * Удаляет все элементы
     * <p></p>
     * Генерирует событие {@link EvListEvent.FullyChanged}
     */
    public void clear(){
        list.clear();
        fireFullyChanged();
    }

    public ImList<A> toImList(){
        return ImList.from(list);
    }

    @Override
    public Iterator<A> iterator() {
        return new ArrayList<>(list).iterator();
    }
}
