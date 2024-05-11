package xyz.cofe.xsd.ui.tbl;

import org.teavm.jso.JSBody;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;
import org.teavm.jso.dom.xml.Element;
import xyz.cofe.ecoll.EvList;
import xyz.cofe.im.iter.ExtIterable;
import xyz.cofe.im.struct.ImList;
import xyz.cofe.im.struct.Tuple2;
import xyz.cofe.xsd.ui.render.RenderedValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class SimpleTable<A> {
    public SimpleTable(){
    }

    private HTMLElement table;
    public HTMLElement getTable(){
        if( table!=null )return table;
        table = HTMLDocument.current().createElement("table").cast();
        return table;
    }

    private HTMLElement thead;
    protected HTMLElement getThead(){
        if( thead!=null )return thead;

        thead = HTMLDocument.current().createElement("thead").cast();
        if( getTable().getChildren().getLength()<1 ) {
            getTable().appendChild(thead);
        }else {
            getTable().insertBefore(thead, getTable().getFirstChild());
        }

        return thead;
    }

    private HTMLElement tbody;
    protected HTMLElement getTbody(){
        if( tbody!=null )return tbody;
        tbody = HTMLDocument.current().createElement("tbody").cast();
        getTable().appendChild(tbody);
        return tbody;
    }

    private TableRows<A> tableRows;
    private TableRows<A> getTableRows(){
        if( tableRows!=null )return tableRows;
        tableRows = new TableRows<>(getTbody());
        return tableRows;
    }

    private static class TableRows<A> implements ExtIterable<TableRow<A>> {
        protected final HTMLElement tbody;
        public TableRows(HTMLElement tbody) {
            if( tbody==null ) throw new IllegalArgumentException("tbody==null");
            this.tbody = tbody;
        }

        @Override
        public Iterator<TableRow<A>> iterator() {
            List<TableRow<A>> list = new ArrayList<>();
            if( tbody.hasChildNodes()) {
                var cn = tbody.getChildNodes();
                for( var i=0;i<cn.getLength();i++ ){
                    var n = cn.item(i);
                    if( "tr".equalsIgnoreCase(n.getLocalName()) ){
                        list.add(new TableRow<>((HTMLElement) n));
                    }
                }
            }
            return list.iterator();
        }

        public void add( TableRow<A> tr ){
            if( tr==null ) throw new IllegalArgumentException("tr==null");
            tbody.appendChild(tr.tableRow);
        }

        public void delete( TableRow<A> tr ){
            if( tr==null ) throw new IllegalArgumentException("tr==null");
            tbody.removeChild(tr.tableRow);
        }

        public void update(TableRow<A> oldRow, TableRow<A> newRow ){
            if( oldRow==null ) throw new IllegalArgumentException("oldRow==null");
            if( newRow==null ) throw new IllegalArgumentException("newRow==null");
            tbody.replaceChild(newRow.tableRow, oldRow.tableRow);
        }

        public void clear(){
            for (TableRow<A> aTableRow : this) {
                delete(aTableRow);
            }
        }
    }

    private static class TableRow<A> {
        @JSBody(params = {"el","dataRow"}, script = "el['_dataRow'] = dataRow;")
        private static native void setDataRow(Element el, Object dataRow);

        @JSBody(params = {"el"}, script = "return el['_dataRow'];")
        private static native Object getDataRow(Element el);

        private TableRow(HTMLElement tableRow) {
            if( tableRow==null ) throw new IllegalArgumentException("tableRow==null");
            this.tableRow = tableRow;
        }

        public static <A> TableRow<A> fromExists(HTMLElement tableRow){
            return new TableRow<A>(tableRow);
        }

        public TableRow(A dataRow){
            this.tableRow = HTMLDocument.current().createElement("tr").cast();
            setDataRow(tableRow, dataRow);
        }

        private final HTMLElement tableRow;

        public ImList<TableRowCell> getCells(){
            ImList<TableRowCell> cells = ImList.empty();
            var coll = tableRow.getChildNodes();
            for( var i=0; i<coll.getLength(); i++ ){
                var n = coll.item(i);
                if( "td".equalsIgnoreCase(n.getLocalName()) ){
                    cells = cells.prepend( new TableRowCell( (HTMLElement) n ) );
                }
            }
            return cells.reverse();
        }

        public void add(TableRowCell tableDataCell) {
            if( tableDataCell ==null ) throw new IllegalArgumentException("td==null");
            tableRow.appendChild(tableDataCell.getCell());
        }
        public void delete(TableRowCell tableDataCell){
            if( tableDataCell ==null ) throw new IllegalArgumentException("td==null");
            tableRow.removeChild(tableDataCell.getCell());
        }

        @SuppressWarnings("unchecked")
        public Optional<A> getDataRow(){
            var ref = getDataRow(tableRow);
            if( ref==null )return Optional.empty();
            return Optional.of( (A)ref );
        }
    }
    private static class TableRowCell {
        public TableRowCell(HTMLElement cell) {
            if( cell==null ) throw new IllegalArgumentException("cell==null");
            this.cell = cell;
        }

        public TableRowCell(){
            this.cell = HTMLDocument.current().createElement("td").cast();
        }

        private final HTMLElement cell;

        public HTMLElement getCell() {
            return cell;
        }
    }

    private EvList<DataColumn<A,?>> dataColumns;
    public EvList<DataColumn<A,?>> getDataColumns(){
        if( dataColumns!=null )return dataColumns;
        System.out.println("build dataColumns");
        dataColumns = new EvList<>();
        dataColumns.onFullyChanged(this::rebuildTableData);
        dataColumns.onInserted(this::onInsertColumn);
        dataColumns.onUpdated(this::onUpdateColumn);
        dataColumns.onDeleted(this::onDeleteColumn);
        rebuildTableHeader();
        return dataColumns;
    }

    private void rebuildTableHeader(){
        for( var tc : getDataColumns() ){
            var th = buildHeaderCell(tc);
            getThead().appendChild(th);
        }
    }

    private final Map<DataColumn<A,?>, List<Runnable>> tcListeners = new HashMap<>();

    private HTMLElement buildHeaderCell(DataColumn<A,?> tableColumn){
        HTMLElement cell = HTMLDocument.current().createElement("th").cast();
        if( tableColumn!=null ) {
            HTMLElement hdrCont = HTMLDocument.current().createElement("div").cast();
            cell.appendChild(hdrCont);

            var frender = tableColumn.getHeaderRender().getValue();
            if (frender != null) {
                var hdr = frender.apply(tableColumn);
                hdrCont.appendChild(hdr);
            }

            var ls1 = tableColumn.getName().onChanged( (old,newName) -> {
                hdrCont.clear();

                var frender1 = tableColumn.getHeaderRender().getValue();
                if (frender1 != null) {
                    var hdr = frender1.apply(tableColumn);
                    hdrCont.appendChild(hdr);
                }
            });

            var ls2 = tableColumn.getHeaderRender().onChanged( (old,newRender) -> {
                hdrCont.clear();

                if (newRender != null) {
                    var hdr = newRender.apply(tableColumn);
                    hdrCont.appendChild(hdr);
                };
            });

            var ls3 = tableColumn.getValueRender().onChanged( (old,valueRender) -> {
                rebuildTableData();
            });

            var lss = tcListeners.computeIfAbsent(tableColumn, x -> new ArrayList<>());
            lss.add(ls1);
            lss.add(ls2);
            lss.add(ls3);
        }
        return cell;
    }

    private void onInsertColumn(int index, DataColumn<A,?> tableColumn){
        var th = buildHeaderCell(tableColumn);
        getThead().appendChild(th);
    }

    private void onUpdateColumn(int index, DataColumn<A,?> oldTableColumn, DataColumn<A,?> newTableColumn){}

    private void onDeleteColumn(int index, DataColumn<A,?> oldTableColumn){
        if(oldTableColumn!=null){
            var ls = tcListeners.remove(oldTableColumn);
            if( ls!=null ){
                ls.forEach(Runnable::run);
            }
        }
    }

    private EvList<A> dataRows;
    public EvList<A> getDataRows(){
        if( dataRows!=null )return dataRows;
        dataRows = new EvList<>();
        dataRows.onFullyChanged(this::rebuildTableData);
        dataRows.onInserted(this::onInsertRow);
        dataRows.onUpdated(this::onUpdateRow);
        dataRows.onDeleted(this::onDeleteRow);
        return dataRows;
    }

    private void rebuildTableData(){
        getTableRows().clear();
        for( var row : getDataRows() ){
            getTableRows().add(buildRow(row));
        }
    }

    private void onInsertRow(int index, A dataRow){
        var tbody = getTbody();

        if( index>=tbody.getChildren().getLength() ){
            tbody.appendChild(buildRow(dataRow).tableRow);
        }else if( index<=0 ){
            tbody.insertBefore(buildRow(dataRow).tableRow, tbody.getFirstChild());
        }else{
            var before = tbody.getChildren().get(index);
            if( before!=null ){
                tbody.insertBefore(buildRow(dataRow).tableRow, before);
            }else{
                tbody.appendChild(buildRow(dataRow).tableRow);
            }
        }
    }

    private void onUpdateRow(int index, A oldDataRow, A newDataRow){
        onDeleteRow(index, oldDataRow);
        onInsertRow(index, newDataRow);
    }

    private void onDeleteRow(int index, A oldDataRow){
        int cnt = tbody.getChildren().getLength();
        if( index<0 || index>=cnt ){
            find(row -> row == oldDataRow).each( t -> tbody.removeChild(t.a()));
        }else{
            var el = tbody.getChildren().item(index);
            if( el instanceof HTMLElement htmlEl ){
                var tr = TableRow.fromExists(htmlEl);
                if( tr.getDataRow().map( existsDataRow -> existsDataRow==oldDataRow).orElse(false) ){
                    tbody.removeChild(el);
                    return;
                }
            }
            find(row -> row == oldDataRow).each( t -> tbody.removeChild(t.a()));
        }
    }

    private ImList<Tuple2<HTMLElement,A>> find(Predicate<A> pred){
        var res = ImList.<Tuple2<HTMLElement,A>>empty();
        int cnt = tbody.getChildren().getLength();
        for( var i=0; i<cnt; i++ ){
            var el = tbody.getChildren().item(i);
            if( el instanceof HTMLElement htmlEl ) {
                var tr = TableRow.<A>fromExists(htmlEl);
                if( tr.getDataRow().map(pred::test).orElse(false) ){
                    var row = tr.getDataRow().get();
                    res = res.prepend(Tuple2.of(htmlEl, row));
                }
            }
        }
        return res.reverse();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private TableRow<A> buildRow(A row){
        TableRow<A> tr = new TableRow<A>(row);
        for( var col : getDataColumns() ){
            TableRowCell tableDataCell = new TableRowCell();
            tr.add(tableDataCell);

            var dataExtr = col.getDataExtractor().getValue();
            if( dataExtr!=null ) {
                var data = dataExtr.apply(row);

                Function render = col.getValueRender().getValue();
                if (render != null) {
                    RenderedValue cell = (RenderedValue) render.apply(data);
                    if(cell!=null){
                        tableDataCell.getCell().appendChild(cell.element());
                    }
                }
            }
        }

        return tr;
    }
}
