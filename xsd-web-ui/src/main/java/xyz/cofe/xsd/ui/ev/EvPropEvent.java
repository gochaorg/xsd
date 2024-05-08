package xyz.cofe.xsd.ui.ev;

/**
 * Событие изменения свойства
 * @param <A> тип свойства
 */
public sealed interface EvPropEvent<A> {
    /**
     * Изменение свойства
     * @param from предыдущее значение
     * @param to текущее значение
     * @param <A> тип
     */
    record Changed<A>( A from, A to ) implements EvPropEvent<A> {}
}
