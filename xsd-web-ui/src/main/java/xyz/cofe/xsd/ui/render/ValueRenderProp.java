package xyz.cofe.xsd.ui.render;

import xyz.cofe.xsd.ui.ev.EvProp;

/**
 * Рендер значения - свойство DataColumn
 * @param <A> Тип значения
 */
public class ValueRenderProp<A> extends EvProp<ValueRender<A>> {
    public ValueRenderProp() {
    }

    public ValueRenderProp(ValueRender<A> initial) {
        super(initial);
    }
}
