package xyz.cofe.xsd.ui.ev;

import xyz.cofe.ecoll.EvListEvent;
import xyz.cofe.ecoll.EvListListener;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;

/**
 * Свойство с уведомлением
 * @param <A> тип свойства
 */
public class EvProp<A> {
    private A value;

    /**
     * Конструктор
     */
    public EvProp(){
    }

    /**
     * Конструктор с начальным значением
     * @param initial начальное значение
     */
    public EvProp(A initial){
        this.value = initial;
    }

    /**
     * Подписчики
     */
    private final WeakHashMap<EvPropListener<A>,Boolean> weakListeners = new WeakHashMap<>();

    /**
     * Подписчики
     */
    private final Set<EvPropListener<A>> strongListeners = new HashSet<>();

    /**
     * Удаление подписчиков
     */
    public void clearListeners(){
        weakListeners.clear();
        strongListeners.clear();
    }

    /**
     * Добавление подписчика
     * @param listener подписчик
     * @return отписка от уведомлений
     */
    public Runnable addListener(EvPropListener<A> listener) {
        if( listener==null ) throw new IllegalArgumentException("listener==null");
        return addListener(false,listener);
    }

    /**
     * Добавление подписчика
     * @param weak true - добавить как weak ссылку
     * @param listener подписчик
     * @return отписка от уведомлений
     */
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

    /**
     * Уведомление об изменении свойства
     * @param listener подписчик: fn(старое значение, новое значение)
     * @return отписка от уведомлений
     */
    public Runnable onChanged(BiConsumer<A,A> listener){
        if( listener==null ) throw new IllegalArgumentException("listener==null");
        return addListener(false, event -> {
            if( event instanceof EvPropEvent.Changed<A> ch ){
                listener.accept(ch.from(), ch.to());
            }
        });
    }

    /**
     * Уведомление об изменении
     * @param from старое значение
     * @param to новое значение
     */
    protected void fireChanged(A from, A to){
        fire(new EvPropEvent.Changed<>(from, to));
    }

    /**
     * Рассылка уведомлений
     * @param ev уведомление
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void fire(EvPropEvent<A> ev){
        // унифицировать до for( var ls : strongListeners ) - нельзя ошибка teavm
        EvPropListener[] listeners = strongListeners.toArray(new EvPropListener[]{});
        for( var ls : listeners ){
            if( ls!=null )ls.evPropEvent(ev);
        }

        listeners = weakListeners.keySet().toArray(new EvPropListener[]{});
        for( var ls : listeners ){
            if( ls!=null )ls.evPropEvent(ev);
        }
    }

    /**
     * Установка новое значения
     * @param value новое значение
     */
    public void setValue(A value){
        A from = this.value;
        this.value = value;
        A to = this.value;
        fireChanged(from, to);
    }

    /**
     * Чтение значения
     * @return значение
     */
    public A getValue(){
        return this.value;
    }
}
