package xyz.cofe.xsd.ui.tbl;

import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;
import xyz.cofe.ecoll.EvList;
import xyz.cofe.im.struct.Tuple2;
import xyz.cofe.xsd.ui.render.RenderedValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class DataGrid<A> {
    private HTMLElement root;

    public HTMLElement getRoot() {
        if (root != null) return root;
        root = HTMLDocument.current().createElement("div").cast();
        root.getStyle().setProperty("display", "grid");
        root.getStyle().setProperty("gridTemplateColumns", gridTemplateColumns());
        root.getStyle().setProperty("overflow","auto");
        getDataColumns().onChanged(() -> {
            root.getStyle().setProperty("gridTemplateColumns", gridTemplateColumns());
        });
        return root;
    }

    private String gridTemplateColumns() {
        return "repeat(" + Math.max(getDataColumns().size(), 1) + ", auto)";
    }

    private EvList<A> dataRows;

    public EvList<A> getDataRows() {
        if (dataRows != null) return dataRows;
        dataRows = new EvList<>();
        dataRows.onInserted(this::onDataRowInserted);
        dataRows.onUpdated(this::onDataRowUpdated);
        dataRows.onDeleted(this::onDataRowDeleted);
        dataRows.onFullyChanged(this::onDataRowsChanged);
        return dataRows;
    }

    private EvList<DataColumn<A, ?>> dataColumns;

    public EvList<DataColumn<A, ?>> getDataColumns() {
        if (dataColumns != null) return dataColumns;
        dataColumns = new EvList<>();

        dataColumns.onInserted(this::onDataColumnInserted);
        dataColumns.onUpdated(this::onDataColumnUpdated);
        dataColumns.onDeleted(this::onDataColumnDeleted);
        dataColumns.onFullyChanged(this::onDataColumnsChanged);

        return dataColumns;
    }

    private final Map<DataColumn<A, ?>, HeaderCell<A>> headers = new HashMap<>();

    private static class HeaderCell<A> {
        public final HTMLElement cell;

        public HeaderCell(int index, DataColumn<A, ?> dc) {
            cell = HTMLDocument.current().createElement("div").cast();
            cell.getStyle().setProperty("grid-row-start", "1");
            cell.getStyle().setProperty("grid-column-start", "" + (index + 1));
            cell.getStyle().setProperty("background-color", "#bfbe9f");
            cell.getStyle().setProperty("position", "sticky");
            cell.getStyle().setProperty("top", "0");

            var hrender = dc.getHeaderRender().getValue();
            if (hrender != null) {
                var c = hrender.apply(dc);
                cell.appendChild(c);
            }
        }

        public void close() {}
    }

    private void onDataColumnInserted(int idx, DataColumn<A, ?> dc) {
        var hc = new HeaderCell<>(idx, dc);
        getRoot().appendChild(hc.cell);
        headers.put(dc, hc);
        rebuildDataRequest();
    }

    private void onDataColumnDeleted(int idx, DataColumn<A, ?> dc) {
        if (dc != null) dataColumnDelete(dc);
        rebuildDataRequest();
    }

    private void dataColumnDelete(DataColumn<A, ?> dc) {
        var hc = headers.get(dc);
        if (hc != null) {
            headers.remove(dc);
            getRoot().removeChild(hc.cell);
            hc.close();
        }
        rebuildDataRequest();
    }

    private void onDataColumnUpdated(int idx, DataColumn<A, ?> dcOld, DataColumn<A, ?> dcNew) {
        if (dcOld != null) dataColumnDelete(dcOld);
        onDataColumnInserted(idx, dcNew);
        rebuildDataRequest();
    }

    private void onDataColumnsChanged() {
        var oldColumns = headers.keySet();
        for (var dc : oldColumns) {
            dataColumnDelete(dc);
        }
        getDataColumns().enumerate().forEach(e -> onDataColumnInserted(e.index(), e.value()));
        rebuildDataRequest();
    }

    private final AtomicInteger rebuildDataRequestCount = new AtomicInteger(0);

    private void rebuildDataRequest() {
        rebuildDataRequestCount.incrementAndGet();
        Window.setTimeout(this::onRebuildDataRequest, 5000);
    }

    private void onRebuildDataRequest() {
        synchronized (rebuildDataRequestCount) {
            var cnt = rebuildDataRequestCount.get();
            if (cnt > 0) {
                rebuildData();
                rebuildDataRequestCount.set(0);
            }
        }
    }

    private class DataRowContainer {
        public final A dataRow;
        public final int dataRowIndex;
        public final Map<DataColumn<A, ?>, DataCellContainer> dataCells;

        public DataRowContainer(A dataRow, int dataRowIndex) {
            this.dataRow = dataRow;
            this.dataRowIndex = dataRowIndex;
            this.dataCells = new HashMap<>();
            getDataColumns().enumerate().forEach(e -> {
                var dc = new DataCellContainer(this, dataRowIndex, e.value(), e.index());
                dataCells.put(e.value(), dc);
            });
        }

        public void close() {
            dataCells.forEach( (dc,dcc) -> dcc.close() );
        }
    }
    private class DataCellContainer {
        public final DataRowContainer row;
        public final int rowIndex;
        public final DataColumn<A, ?> column;
        public final int columnIndex;
        public final HTMLElement cell;
        //private List<Runnable> cell

        @SuppressWarnings({"unchecked", "rawtypes"})
        public DataCellContainer(DataRowContainer row, int rowIndex, DataColumn<A, ?> column, int columnIndex) {
            if( row==null ) throw new IllegalArgumentException("row==null");
            if( column==null ) throw new IllegalArgumentException("column==null");

            this.row = row;
            this.rowIndex = rowIndex;

            this.column = column;
            this.columnIndex = columnIndex;

            this.cell = HTMLDocument.current().createElement("div");
            this.cell.getStyle().setProperty("grid-column-start", "" + (columnIndex + 1));
            this.cell.getStyle().setProperty("grid-row-start", "" + (rowIndex + 1));

            var valueExtr = column.getDataExtractor().getValue();
            if( valueExtr!=null ){
                Object value = valueExtr.apply(row.dataRow);
                if( value!=null ){
                    Function valueRender = column.getValueRender().getValue();
                    if( valueRender!=null ){
                        RenderedValue renderedValue = (RenderedValue)valueRender.apply(value);
                        cell.appendChild(renderedValue.element());
                    }
                }
            }
        }

        public void close(){
        }
    }

    private final List<DataRowContainer> dataRowContainers = new ArrayList<>();

    private void onDataRowInserted(int idx, A dataRow) {
        if (idx >= dataRowContainers.size()) {
            appendDataRow(dataRow);
        } else if (idx < 0) {
            appendDataRow(dataRow);
        } else {
            appendDataRow(dataRow, idx);
        }
    }

    private void appendDataRow(A dataRow) {
        var idx = dataRowContainers.size();
        appendDataRow(dataRow, idx);
    }

    private void appendDataRow(A dataRow, int idx) {
        var drc = new DataRowContainer(dataRow, idx);
        dataRowContainers.add(idx, drc);
        mount(drc);
    }

    private void mount(DataRowContainer drc){
        drc.dataCells.forEach((i, c) -> {
            getRoot().appendChild(c.cell);
        });
    }

    private void unmount(DataRowContainer drc){
        drc.dataCells.forEach((dc, dcc) -> {getRoot().removeChild(dcc.cell);});
        drc.close();
    }

    private void onDataRowDeleted(int idx, A dataRow) {
        // Первая попытка - доступ по индексу
        var drc1 = dataRowContainers.get(idx);
        if (drc1 != null && drc1.dataRow == dataRow) {
            unmount(drc1);
            dataRowContainers.remove(idx);
            return;
        }

        // Вторая попытка
        // Полный перебор
        List<Tuple2<Integer, DataRowContainer>> dccRemoveset = new ArrayList<>();
        var ridx = -1;
        for (var drc2 : dataRowContainers) {
            ridx++;
            if (drc2.dataRow == dataRow) {
                dccRemoveset.add(0, Tuple2.of(ridx, drc2));
            }
        }

        for (var drc3t : dccRemoveset) {
            int ridx2 = drc3t.a();
            dataRowContainers.remove(ridx2);
            unmount(drc3t.b());
        }
    }

    private void onDataRowUpdated(int idx, A oldDataRow, A newDataRow) {
        onDataRowDeleted(idx, oldDataRow);
        onDataRowInserted(idx, newDataRow);
    }

    private void onDataRowsChanged() {
        rebuildDataRequest();
    }

    private void rebuildData() {
        for( var drc : dataRowContainers ){
            unmount(drc);
        }
        dataRowContainers.clear();
        getDataRows().enumerate().forEach( en -> onDataRowInserted(en.index(), en.value()) );
    }
}
