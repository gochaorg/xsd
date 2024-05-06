package xyz.cofe.xsd.ui.tbl;

import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;
import xyz.cofe.im.iter.ExtIterable;
import xyz.cofe.im.struct.ImList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Table {
    public Table(){
    }

    private HTMLElement table;
    public HTMLElement getTable(){
        if( table!=null )return table;
        table = HTMLDocument.current().createElement("table").cast();
        return table;
    }

    private HTMLElement thead;
    public HTMLElement getThead(){
        if( thead!=null )return thead;
        thead = HTMLDocument.current().createElement("thead").cast();
        getTable().appendChild(thead);
        return thead;
    }

    private HTMLElement tbody;
    public HTMLElement getTbody(){
        if( tbody!=null )return tbody;
        tbody = HTMLDocument.current().createElement("tbody").cast();
        getTable().appendChild(tbody);
        return tbody;
    }

    private TableRows tableRows;
    public TableRows getRows(){
        if( tableRows!=null )return tableRows;
        tableRows = new TableRows(getTbody());
        return tableRows;
    }

    public static class TableRows implements ExtIterable<TR> {
        protected final HTMLElement tbody;
        public TableRows(HTMLElement tbody) {
            if( tbody==null ) throw new IllegalArgumentException("tbody==null");
            this.tbody = tbody;
        }

        @Override
        public Iterator<TR> iterator() {
            List<TR> list = new ArrayList<>();
            if( tbody.hasChildNodes()) {
                var cn = tbody.getChildNodes();
                for( var i=0;i<cn.getLength();i++ ){
                    var n = cn.item(i);
                    if( "tr".equalsIgnoreCase(n.getLocalName()) ){
                        list.add(new TR((HTMLElement) n));
                    }
                }
            }
            return list.iterator();
        }

        public void add( TR tr ){
            if( tr==null ) throw new IllegalArgumentException("tr==null");
            tbody.appendChild(tr.row);
        }

        public void delete( TR tr ){
            if( tr==null ) throw new IllegalArgumentException("tr==null");
            tbody.removeChild(tr.row);
        }

        public void update( TR oldRow, TR newRow ){
            if( oldRow==null ) throw new IllegalArgumentException("oldRow==null");
            if( newRow==null ) throw new IllegalArgumentException("newRow==null");
            tbody.replaceChild(newRow.row, oldRow.row);
        }
    }

    public static class TR {
        public TR(HTMLElement row) {
            if( row==null ) throw new IllegalArgumentException("row==null");
            this.row = row;
        }

        public TR(){
            this.row = HTMLDocument.current().createElement("tr").cast();
        }

        private final HTMLElement row;

        public ImList<TD> getCells(){
            ImList<TD> cells = ImList.empty();
            var coll = row.getChildNodes();
            for( var i=0; i<coll.getLength(); i++ ){
                var n = coll.item(i);
                if( "td".equalsIgnoreCase(n.getLocalName()) ){
                    cells = cells.prepend( new TD( (HTMLElement) n ) );
                }
            }
            return cells.reverse();
        }

        public void add(TD td) {
            if( td==null ) throw new IllegalArgumentException("td==null");
            row.appendChild(td.getCell());
        }
        public void delete(TD td){
            if( td==null ) throw new IllegalArgumentException("td==null");
            row.removeChild(td.getCell());
        }
    }
    public static class TD {
        public TD(HTMLElement cell) {
            if( cell==null ) throw new IllegalArgumentException("cell==null");
            this.cell = cell;
        }

        public TD(){
            this.cell = HTMLDocument.current().createElement("td").cast();
        }

        private final HTMLElement cell;

        public HTMLElement getCell() {
            return cell;
        }
    }

}
