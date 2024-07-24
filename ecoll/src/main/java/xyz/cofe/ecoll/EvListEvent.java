package xyz.cofe.ecoll;

/**
 * Событие изменения списка
 * @param <A> тип элемента списка
 */
public sealed interface EvListEvent<A> {
    /**
     * Событие добавления элемента в список
     * @param index индекс элемента
     * @param item элемент списка
     * @param <A> тип элемента списка
     */
    record Inserted<A>(int index, A item) implements EvListEvent<A> {}

    /**
     * Событие обновления элемента в списоке
     * @param index индекс элемента
     * @param item элемент списка
     * @param old предыдущий элемент списка
     * @param <A> тип элемента списка
     */
    record Updated<A>(int index, A item, A old) implements EvListEvent<A> {}

    /**
     * Событие удаления элемента из списка
     * @param index индекс элемента
     * @param old элемент списка
     * @param <A> тип элемента списка
     */
    record Deleted<A>(int index, A old) implements EvListEvent<A> {}

    /**
     * Событие изменение полного состава списка
     * @param <A> тип элемента списка
     */
    record FullyChanged<A>() implements EvListEvent<A> {}
}
