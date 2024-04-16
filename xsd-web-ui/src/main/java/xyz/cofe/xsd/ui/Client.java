package xyz.cofe.xsd.ui;

import org.teavm.jso.dom.html.HTMLButtonElement;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;
import org.teavm.jso.dom.html.HTMLInputElement;
import org.teavm.jso.ajax.XMLHttpRequest;
import xyz.cofe.im.struct.Result;
import xyz.cofe.xsd.om.XsdDoc;
import xyz.cofe.xsd.om.ldr.XsdLoader;
import xyz.cofe.xsd.om.xml.print.XmlPrinter;
import xyz.cofe.xsd.om.xml.jso.XmlDocJSOAdapter;

import java.net.URI;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class Client {
    public static void main(String[] args) {
        new Client().buildUI();
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

        HTMLButtonElement butGet = HTMLDocument.current().createElement("button").cast();
        butGet.setInnerText("xml get");
        buttons.appendChild(butGet);

        HTMLElement pre = HTMLDocument.current().createElement("pre");
        div.appendChild(pre);
        HTMLElement log = HTMLDocument.current().createElement("div");
        div.appendChild(log);

        butGet.addEventListener("click", evt -> {
            getXml(urlAddr.getValue(), pre);
        });

        HTMLButtonElement xsdGetBut = HTMLDocument.current().createElement("button").cast();
        xsdGetBut.setInnerText("xsd get");
        buttons.appendChild(xsdGetBut);
        xsdGetBut.addEventListener("click", evt -> {
            loadXsd(urlAddr.getValue(), txt -> {
                HTMLElement line = HTMLDocument.current().createElement("div");
                line.setInnerText(txt);
                log.appendChild(line);
            });
        });

        HTMLButtonElement threadBut = HTMLDocument.current().createElement("button").cast();
        threadBut.setInnerText("thread");
        buttons.appendChild(threadBut);
        threadBut.addEventListener("click", evt -> {
            threadTest(txt -> {
                HTMLElement line = HTMLDocument.current().createElement("div");
                line.setInnerText(txt);
                log.appendChild(line);
            });
        });
    }

    private void getXml(String addr, HTMLElement result) {
        var xhr = XMLHttpRequest.create();
        xhr.onComplete(() -> {
            var str = xhr.getResponseText();
            var doc = XmlDocJSOAdapter.parse(str);

            var buff = new StringBuilder();
            new XmlPrinter(buff).print(doc);

            result.setInnerText(
                "parsed\n"+buff.toString()
            );
        });
        xhr.onError(err->{
            System.out.println("err!");
        });
        xhr.open("GET", addr);
        xhr.send();
    }

    private Optional<String> syncGet(String addr){
        var res = new AtomicReference<String>();
        var ready = new AtomicBoolean(false);
        var complete = new AtomicBoolean(false);

        var xhr = XMLHttpRequest.create();
        xhr.onComplete(() -> {
            System.out.println("xhr.onComplete");
            var str = xhr.getResponseText();
            res.set(str);
            complete.set(true);
            ready.set(true);
        });
        xhr.onError(err->{
            System.out.println("xhr.onError");
            ready.set(true);
        });

        System.out.println("xhr.open");
        xhr.open("GET", addr, false);

        System.out.println("xhr.send");
        xhr.send();

//        while (!ready.get()){
//            if( xhr.getReadyState() >= XMLHttpRequest.DONE )break;
////            try {
////                //noinspection BusyWait
////                //Thread.currentThread().sleep(10);
////            } catch (InterruptedException e) {
////                break;
////            }
//        }

        System.out.println("complete.get()");
        if(complete.get()){
            System.out.println("complete.get() = true");
            return Optional.of(res.get());
        }

        System.out.println("complete.get() = false");
        return Optional.empty();
    }

    private void loadXsd(String xsdUrl, Consumer<String> log){
        var xsdLdr = new XsdLoader(
            uri -> {
                log.accept("try load "+uri);

                var xsdTextOpt = syncGet(uri.toString());
                log.accept("get "+uri+" "+(xsdTextOpt.isPresent() ? "succ" : "fail"));
                if(xsdTextOpt.isEmpty()) {
                    log.accept("not loaded");
                    return Result.err("not loaded from " + uri);
                }

                log.accept("loaded");
                var xmlDoc = XmlDocJSOAdapter.parse(xsdTextOpt.get());
                var xsdDoc = new XsdDoc(xmlDoc);

                return Result.ok(xsdDoc);
            },
            ((uri, uri2) -> {
                var uri3 = uri.resolve(uri2);
                log.accept("url.resolve "+uri3);
                return uri3;
            })
        );

        var uri = URI.create(xsdUrl);
        log.accept("URI.create "+uri);
        var xsdLoadRes = xsdLdr.load(uri);
        if( xsdLoadRes instanceof Result.Ok xsdSuccLoad ){
            log.accept("succ loaded from "+xsdUrl);
        }else{
            log.accept("not loaded from "+xsdUrl);
        }
    }

    private void threadTest(Consumer<String> log) {
        new Thread(()->{
            try {
                log.accept("sleeping");
                Thread.sleep(1000);
                log.accept("sleep finished");
            } catch (InterruptedException e) {
                log.accept(e.toString());
            }
        }).start();
    }
}
