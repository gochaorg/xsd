package xyz.cofe.xsd.ui.tbl;

import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;
import xyz.cofe.xsd.ui.ev.EvProp;
import xyz.cofe.xsd.ui.render.RenderedValue;
import xyz.cofe.xsd.ui.render.ValueRender;

import java.util.function.Function;

/**
 * Колонка (поле) данных
 * @param <A> Структурный тип
 * @param <B> Поле структурного типа
 */
public class DataColumn<A, B> {
    /**
     * Конструктор
     */
    public DataColumn(){
    }

    /**
     * Конструктор
     * @param name имя поля
     * @param extract извлечение данных из структурного типа
     */
    public DataColumn(String name, Function<A, B> extract){
        if( name==null ) throw new IllegalArgumentException("name==null");
        this.name.setValue(name);

        if( extract==null ) throw new IllegalArgumentException("extract==null");
        this.dataExtractor.setValue(extract);
    }

    //region name : EvProp<String> - имя колонки
    private final EvProp<String> name = new EvProp<>("name");

    /**
     * Возвращает имя колонки
     * @return имя колонки
     */
    public EvProp<String> getName() {return name;}
    //endregion

    //region dataExtractor : EvProp<Function<A,B>> - Извлечение данных из структурного типа
    private final EvProp<Function<A,B>> dataExtractor = new EvProp<>();

    /**
     * Извлечение данных из структурного типа
     * @return функция извлечения
     */
    public EvProp<Function<A, B>> getDataExtractor() {
        return dataExtractor;
    }
    //endregion

    //region headerRender : EvProp<Function<TableColumn<A, ?>, HTMLElement>> - рендер заголовка
    private final EvProp<Function<DataColumn<A, ?>, HTMLElement>> headerRender = new EvProp<>(tc -> {
        HTMLElement el = HTMLDocument.current().createElement("div");

        var curName = tc.getName().getValue();
        el.setInnerText(curName!=null ? curName : "?");

        tc.getName().onChanged( (from,to) -> {
            el.setInnerText(to!=null ? to : "?");
        });

        return el;
    });

    public EvProp<Function<DataColumn<A, ?>, HTMLElement>> getHeaderRender() {
        return headerRender;
    }
    //endregion

    //region valueRender : EvProp<Function<B, HTMLElement>> - рендер значения
    private final EvProp<ValueRender<B>> valueRender = new EvProp<>(ValueRender.toStringRender("div"));

    public EvProp<ValueRender<B>> getValueRender() {
        return valueRender;
    }
    //endregion
}
