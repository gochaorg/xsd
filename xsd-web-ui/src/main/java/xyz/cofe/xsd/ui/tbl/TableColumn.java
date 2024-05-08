package xyz.cofe.xsd.ui.tbl;

import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;
import xyz.cofe.xsd.ui.ev.EvProp;

import java.util.function.Function;

public class TableColumn<A, B> {
    public TableColumn(){
    }

    public TableColumn(String name, Function<A, B> extract){
        if( name==null ) throw new IllegalArgumentException("name==null");
        this.name.setValue(name);

        if( extract==null ) throw new IllegalArgumentException("extract==null");
        this.dataExtractor.setValue(extract);
    }

    //region name : EvProp<String>
    private final EvProp<String> name = new EvProp<>("name");

    public EvProp<String> getName() {return name;}
    //endregion

    //region dataExtractor : EvProp<Function<A,B>>
    private final EvProp<Function<A,B>> dataExtractor = new EvProp<>();

    public EvProp<Function<A, B>> getDataExtractor() {
        return dataExtractor;
    }
    //endregion

    //region headerRender : EvProp<Function<TableColumn<A, ?>, HTMLElement>>
    private final EvProp<Function<TableColumn<A, ?>, HTMLElement>> headerRender = new EvProp<>( tc -> {
        HTMLElement el = HTMLDocument.current().createElement("div");

        var curName = tc.getName().getValue();
        el.setInnerText(curName!=null ? curName : "?");

        tc.getName().onChanged( (from,to) -> {
            el.setInnerText(to!=null ? to : "?");
        });

        return el;
    });

    public EvProp<Function<TableColumn<A, ?>, HTMLElement>> getHeaderRender() {
        return headerRender;
    }
    //endregion

    //region valueRender : EvProp<Function<B, HTMLElement>>
    private final EvProp<Function<B, HTMLElement>> valueRender = new EvProp<>(
        value -> {
            var el = HTMLDocument.current().createElement("div");
            el.setInnerText(value != null ? value.toString() : "null");
            return el;
        }
    );

    public EvProp<Function<B, HTMLElement>> getValueRender() {
        return valueRender;
    }
    //endregion
}
