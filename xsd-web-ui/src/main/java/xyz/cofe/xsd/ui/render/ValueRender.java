package xyz.cofe.xsd.ui.render;

import org.teavm.jso.dom.html.HTMLDocument;

import java.util.Optional;
import java.util.function.Function;

/**
 * Функция рендера значения
 * @param <A> тип значения
 */
public interface ValueRender<A> extends Function<A, RenderedValue> {
    public static <A> ValueRender<A> toStringRender(String tagName) {
        return value -> {
            var el = HTMLDocument.current().createElement(
                 tagName!=null ? tagName : "div"
            );
            if( value!=null ){
                el.setInnerText(value.toString());
            }
            return new RenderedValue(el, Optional.empty());
        };
    }
}
