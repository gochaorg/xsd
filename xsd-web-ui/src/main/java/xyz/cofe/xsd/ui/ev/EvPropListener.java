package xyz.cofe.xsd.ui.ev;

/**
 * Подписчик на событие изменении
 * @param <A> тип свойства
 */
public interface EvPropListener<A> {
    /**
     * Получение уведомления
     * @param event уведомление
     */
    void evPropEvent(EvPropEvent<A> event);
}
