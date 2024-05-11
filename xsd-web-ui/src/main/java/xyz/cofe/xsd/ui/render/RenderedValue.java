package xyz.cofe.xsd.ui.render;

import org.teavm.jso.dom.html.HTMLElement;

import java.util.Optional;

/**
 * Отрисованное значение
 * @param element html значение
 * @param unmount функция демонтированное
 */
public record RenderedValue(HTMLElement element, Optional<Runnable> unmount) {}
