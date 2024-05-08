package xyz.cofe.xsd.ui;

import org.teavm.jso.ajax.ProgressEvent;
import org.teavm.jso.dom.html.HTMLButtonElement;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;
import org.teavm.jso.dom.html.HTMLInputElement;
import org.teavm.jso.ajax.XMLHttpRequest;
import xyz.cofe.im.struct.Result;
import xyz.cofe.xsd.cmpl.XsdCompile;
import xyz.cofe.xsd.om.XsdSchema;
import xyz.cofe.xsd.om.ldr.XsdLoader;
import xyz.cofe.xml.jso.XmlDocJSOAdapter;
import xyz.cofe.xsd.ui.files.FilesClient;
import xyz.cofe.xsd.ui.tbl.Table;
import xyz.cofe.xsd.ui.tbl.TableColumn;

import java.net.URI;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class Client {
    public static void main(String[] args) {
        // Запускаем отдельный поток
        // Надо что бы корректно работало Thread.sleep в TeaVM
        new Thread(() -> {
            new Client().buildUI();
        }).start();
    }

    private void buildUI() {
        var div = HTMLDocument.current().createElement("div");
        HTMLDocument.current().getBody().appendChild(div);

        HTMLInputElement urlAddr = HTMLDocument.current().createElement("input").cast();

        urlAddr.setType("text");
        urlAddr.setValue("/xsd/ed/cbr_ed101_v2024.4.0.xsd");
        urlAddr.getStyle().setCssText("display:block; width:10cm;");
        div.appendChild(urlAddr);

        var buttons = HTMLDocument.current().createElement("div");
        HTMLDocument.current().getBody().appendChild(buttons);

        HTMLElement pre = HTMLDocument.current().createElement("pre");
        div.appendChild(pre);
        HTMLElement log = HTMLDocument.current().createElement("div");
        div.appendChild(log);

        HTMLButtonElement xsdGetBut = HTMLDocument.current().createElement("button").cast();
        xsdGetBut.setInnerText("xsd get");
        buttons.appendChild(xsdGetBut);
        xsdGetBut.addEventListener("click", evt -> {
            loadXsd(urlAddr.getValue(), txt -> {
                HTMLElement line = HTMLDocument.current().createElement("div");
                line.setInnerText(txt);
                log.appendChild(line);
            }, txt -> {
                var txt1 = pre.getInnerText();
                pre.setInnerText(txt1 + txt);
            });
        });

        /////////////
        HTMLButtonElement listFilesBut = HTMLDocument.current().createElement("button").cast();
        buttons.appendChild(listFilesBut);
        listFilesBut.setInnerText("files");

        listFilesBut.addEventListener("click", evt -> {
            HTMLElement tcont = HTMLDocument.current().createElement("div");

            HTMLButtonElement but = HTMLDocument.current().createElement("button").cast();
            but.setInnerText("close");
            tcont.appendChild(but);

            Table<FilesClient.PathObj> table = new Table<>();
            table.getDataColumns().insert( new TableColumn<>( "name", FilesClient.PathObj::name));
            table.getDataColumns().insert( new TableColumn<>("dir", FilesClient.PathObj::isDirectory) );

            var tbody = table.getTable().querySelector("tbody");

            tcont.appendChild(table.getTable());
            div.appendChild(tcont);

            but.addEventListener("click",ev -> {
                div.removeChild(tcont);
            });

            var fc = new FilesClient();

            fc.listFiles(urlAddr.getValue()).each(po -> {
                table.getDataRows().insert(po);

//                Table.TD name, type;
//                if (po instanceof FilesClient.PathObj.Directory d) {
//
//                    name = new Table.TD();
//                    name.getCell().setInnerText(d.name());
//
//                    type = new Table.TD();
//                    type.getCell().setInnerText("dir");
//
//                    row.add(name);
//                    row.add(type);
//
//                } else if (po instanceof FilesClient.PathObj.File f) {
//                    name = new Table.TD();
//                    name.getCell().setInnerText(f.name());
//
//                    type = new Table.TD();
//                    type.getCell().setInnerText("file");
//
//                    row.add(name);
//                    row.add(type);
//                }
//
//                table.getTableRows().add(row);
            });
        });
    }

    private Optional<String> syncGet(String addr) {
        var res = new AtomicReference<String>();
        var ready = new AtomicBoolean(false);
        var complete = new AtomicBoolean(false);

        var xhr = XMLHttpRequest.create();
        xhr.addEventListener("readystatechange", evt -> {
            if(xhr.getReadyState() == XMLHttpRequest.DONE){
                System.out.println("xhr.onComplete");
                var str = xhr.getResponseText();
                res.set(str);
                complete.set(true);
                ready.set(true);
            }
        });
        xhr.addEventListener("error", evt -> {
            System.out.println("xhr.onError");
            ready.set(true);
        });

        System.out.println("xhr.open");
        xhr.open("GET", addr, false);

        System.out.println("xhr.send");
        xhr.send();

        System.out.println("complete.get()");
        if (complete.get()) {
            System.out.println("complete.get() = true");
            return Optional.of(res.get());
        }

        System.out.println("complete.get() = false");
        return Optional.empty();
    }

    private void loadXsd(String xsdUrl, Consumer<String> log, Consumer<String> res) {
        var xsdLdr = new XsdLoader(
            uri -> {
                log.accept("try load " + uri);

                var xsdTextOpt = syncGet(uri.toString());
                log.accept("get " + uri + " " + (xsdTextOpt.isPresent() ? "succ" : "fail"));
                if (xsdTextOpt.isEmpty()) {
                    log.accept("not loaded");
                    return Result.err("not loaded from " + uri);
                }

                log.accept("loaded");
                var xmlDoc = XmlDocJSOAdapter.parse(xsdTextOpt.get());
                var xsdDoc = new XsdSchema(xmlDoc);

                return Result.ok(xsdDoc);
            },
            ((uri, uri2) -> {
                var uri3 = uri.resolve(uri2);
                log.accept("url.resolve " + uri3);
                return uri3;
            })
        );

        var uri = URI.create(xsdUrl);
        log.accept("URI.create " + uri);

        xsdLdr.load(uri).fold(schema -> {
            log.accept("succ loaded from " + xsdUrl);

            schema.getElements().head().ifPresent(el -> {
                log.accept("compile " + el.getName().toString());
                XsdCompile compile = new XsdCompile();
                res.accept(compile.compile(el));
            });

            return null;
        }, err -> {
            log.accept("not loaded from " + xsdUrl);
            return null;
        });
    }
}
