package xyz.cofe.xsd.ui;

import org.teavm.jso.dom.html.HTMLButtonElement;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;
import org.teavm.jso.dom.html.HTMLInputElement;
import org.teavm.jso.ajax.XMLHttpRequest;
import xyz.cofe.xsd.om.xml.print.XmlPrinter;
import xyz.cofe.xsd.om.xml.jso.XmlDocJSOAdapter;

public class Client {
    public static void main(String[] args) {
        new Client().buildUI();
    }

    private void buildUI() {
        var div = HTMLDocument.current().createElement("div");
        HTMLDocument.current().getBody().appendChild(div);

        HTMLInputElement urlAddr = HTMLDocument.current().createElement("input").cast();
        urlAddr.setType("text");
        urlAddr.setValue("/xsd/xml.xsd");
        div.appendChild(urlAddr);

        HTMLButtonElement but = HTMLDocument.current().createElement("button").cast();
        but.setInnerText("get");
        div.appendChild(but);

        HTMLElement res = HTMLDocument.current().createElement("pre");
        div.appendChild(res);
        but.addEventListener("click", evt -> {
            getPlainText(urlAddr.getValue(), res);
        });
    }

    private void getPlainText(String addr, HTMLElement result) {
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
}
