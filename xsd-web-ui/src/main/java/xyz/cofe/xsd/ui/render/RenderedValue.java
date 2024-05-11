package xyz.cofe.xsd.ui.render;

import org.teavm.jso.dom.html.HTMLElement;

import java.util.Optional;
import java.util.function.Function;

/**
 * Отрисованное значение
 * @param element html значение
 * @param unmount функция демонтированное
 */
public record RenderedValue(HTMLElement element, Optional<Runnable> unmount) {
    //public RenderedValue map(Function<HTMLElement, HTMLElement> injector)
}
